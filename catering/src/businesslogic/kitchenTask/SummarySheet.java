package businesslogic.kitchenTask;

import businesslogic.CatERing;
import businesslogic.event.ServiceInfo;
import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
import businesslogic.recipe.Procedure;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SummarySheet {
	private ObservableList<Task> tasks;
	private ServiceInfo service;
	private int id;

	public SummarySheet(ServiceInfo service) {
		// si puù cambiare, se nell'update inviamo anche il service
		this.service = service;
		this.tasks = FXCollections.observableArrayList();
		List<Section> sections = service.getMenu().getSections();
		for(Section section : sections)
		{
			List<MenuItem> sectionItems = section.getItems();
			for(MenuItem item : sectionItems)
			{
				Procedure procedure = item.getItemRecipe();
				Task task = new Task(procedure);
				tasks.add(task);
			}
		}
		List<MenuItem> freeItems = service.getMenu().getFreeItems();
		for(MenuItem item : freeItems)
		{
			Procedure procedure = item.getItemRecipe();
			Task task = new Task(procedure);
			tasks.add(task);
		}
	}
	//secondo costruttore che inizializza il foglio riepilogativo in base al servizio e all'id del foglio stesso
	public SummarySheet(ServiceInfo service, int id) {
		new SummarySheet(service);
		tasks = FXCollections.observableArrayList();
		List<Task> freeTask = Task.getAddedTasks(id);
		tasks.addAll(freeTask);
	}


	public static SummarySheet loadSummarySheet(ServiceInfo service) {
		//altrimenti non funzionava se mettevo una var sheet
		int id = getSummarySheetId(service);//devo prendere l'id così perchè il currentSheet sarà settato DOPO questo metodo loadSummarySheet
		final SummarySheet[] sheet = {null};
		String query = "SELECT * FROM SummarySheet WHERE service_id = "+service.getId();
		PersistenceManager.executeQuery(query, new ResultHandler() {
			@Override
			public void handle(ResultSet rs) throws SQLException {
				sheet[0] = new SummarySheet(service,id);
				sheet[0].id = rs.getInt("id");
				sheet[0].service = service;
			}
		});
		return sheet[0];
	}

	//dato un servizio ottengo l'id del suo summary sheet
	private static int getSummarySheetId(ServiceInfo service) {
		String select = "SELECT * FROM catering.SummarySheet WHERE service_id = " +service.getId();
		final int[] id = new int[1];
		PersistenceManager.executeQuery(select, new ResultHandler() {
			@Override
			public void handle(ResultSet rs) throws SQLException {

				id[0] = rs.getInt("id");
			}
		});
		return id[0];
	}

	public static void saveNewSummarySheet(SummarySheet currentSheet) {
		String menuInsert = "INSERT INTO catering.SummarySheet (service_id) VALUES (?);";
		int[] result = PersistenceManager.executeBatchUpdate(menuInsert, 1, new BatchUpdateHandler() {
			@Override
			public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
				ps.setInt(1, currentSheet.service.getId());
			}

			@Override
			public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
				// should be only one
				if (count == 0) {
					currentSheet.id = rs.getInt(1);
				}
			}
		});

		if (result[0] > 0) { // sheet effettivamente inserito
			// salva i task
			if (currentSheet.getTasks().size() > 0) {
				Task.saveAllNewTasks(currentSheet.getId(), currentSheet.getTasks());
			}

			//loadedMenus.put(m.id, m);
		}
	}

	public static void deleteSummarySheet(SummarySheet currentSheet) {
		String summarySheetDelete = "DELETE FROM catering.SummarySheet WHERE id = "+currentSheet.getId();
		PersistenceManager.executeUpdate(summarySheetDelete);
		// elimino i task riferiti al summary sheet appena rimosso
		if (currentSheet.getTasks().size() > 0) {
			Task.deleteRelatedTasks(currentSheet.getId());//elimino i task relativi al summarySheet
		}
	}

	public static void saveTasksOrder(SummarySheet currentSheet) {
		String upd = "UPDATE tasks SET position = ? WHERE id = ?";
		PersistenceManager.executeBatchUpdate(upd, currentSheet.tasks.size(), new BatchUpdateHandler() {
			@Override
			public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
				ps.setInt(1, batchCount);
				ps.setInt(2, currentSheet.tasks.get(batchCount).getId());
			}

			@Override
			public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
				// no generated ids to handle
			}
		});
	}


	public ObservableList<Task> getTasks() {
		return tasks;
	}

	public ServiceInfo getService() {
		return service;
	}

	public int getId() {
		return id;
	}

    public boolean hasTask(Task task) {
		return tasks.contains(task);
    }

	public void removeTask(Task task) {
		tasks.remove(task);
	}

	public int getTasksCount() {
		return tasks.size();
	}

	public void moveTask(Task task, int newpos) {
		tasks.remove(task);
		tasks.add(newpos, task);
	}
}
