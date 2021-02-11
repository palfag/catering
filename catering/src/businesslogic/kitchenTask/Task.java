package businesslogic.kitchenTask;

import businesslogic.CatERing;
import businesslogic.recipe.Preparation;
import businesslogic.recipe.Procedure;
import businesslogic.recipe.Recipe;
import businesslogic.user.User;
import businesslogic.workShift.WorkShift;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Task {
	private int id;
	private User cook;
	private WorkShift kitchenShift;
	private Procedure procedure;
	private boolean completed;
	private String quantity = "non fornita";
	private int estimateTime;

	public Task(Procedure procedure) {
		this.procedure = procedure;
	}




    private void setId(int id) {
		this.id=id;
	}

	public User getCook() {
		return cook;
	}

	public void setCook(User cook) {
		this.cook = cook;
	}

	public WorkShift getKitchenShift() {
		return kitchenShift;
	}

	public void setKitchenShift(WorkShift kitchenShift) {
		this.kitchenShift = kitchenShift;
	}

	public Procedure getProcedure() {
		return procedure;
	}

	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public int getEstimateTime() {
		return estimateTime;
	}

	public void setEstimateTime(int estimateTime) {
		this.estimateTime = estimateTime;
	}

	public static void saveAllNewTasks(int sheet_id, List<Task> tasks) {
		String taskInsert = "INSERT INTO catering.Tasks (completed, quantity, estimateTime, cook_id, kitchenShift_id, recipe_id, preparation_id, summary_sheet_id, position) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
		PersistenceManager.executeBatchUpdate(taskInsert, tasks.size(), new BatchUpdateHandler() {
			@Override
			public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
				Task taskToInsert = tasks.get(batchCount);
				ps.setBoolean(1, taskToInsert.completed);
				ps.setString(2, taskToInsert.quantity);
				ps.setInt(3, taskToInsert.estimateTime);

				//visto che inizialmente il task può non essere assegnato ne a un cuoco ne a un turno
				if(taskToInsert.cook!=null)
					ps.setInt(4, taskToInsert.cook.getId());
				else
					ps.setInt(4, 0);
				if(taskToInsert.kitchenShift!=null)
					ps.setInt(5, taskToInsert.kitchenShift.getId());
				else
					ps.setInt(5, 0);

				if(taskToInsert.procedure.getClass() == Recipe.class) {//se è una ricetta mette l'id alla ricetta sul db viceversa lo mette all'altro
					ps.setInt(6, ((Recipe) taskToInsert.procedure).getId());
					ps.setInt(7, 0);
				}
				else
				{
					ps.setInt(6, 0);
					ps.setInt(7, ((Preparation)taskToInsert.procedure).getId());
				}
				ps.setInt(8, sheet_id);
				ps.setInt(9, batchCount); //aggiunta posizione in base a pos. nella list
			}

			@Override
			public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
				tasks.get(count).id = rs.getInt(1);
			}
		});
	}
	//salvo i nuovi task creati quando faccio inserisciProcedura
	public static void saveNewTask(SummarySheet sheet, Task task) {
		List<Task> tasks = new ArrayList<>();
		tasks.add(task);
		saveAllNewTasks(sheet.getId() , tasks);
		SummarySheet.saveTasksOrder(sheet);
	}

	//ottengo tutti i task di un summary sheet, se usavo il primo costruttore di summarySheet prendeva solo i task basati sul menu
	public static List<Task> getAddedTasks(int id) {
		String taskSelection = "SELECT * FROM catering.Tasks WHERE summary_sheet_id = " +id + " ORDER BY position";
		ArrayList<Task> tasks = new ArrayList<>();
		PersistenceManager.executeQuery(taskSelection, new ResultHandler() {
			@Override
			public void handle(ResultSet rs) throws SQLException {
				List<Recipe> allRecipes = CatERing.getInstance().getRecipeManager().getRecipes();//prendo tutte le ricette/preparazioni
				List<Preparation> allPeparations = CatERing.getInstance().getRecipeManager().getPreparations();

				do {
					//int procedureId = (rs.getInt("recipe_id")!= 0) ? rs.getInt("recipe_id") : rs.getInt("preparation_id");
					Task t;
					if (rs.getInt("recipe_id")!=0){//se il task da creare è legato ad una ricetta
						Recipe r = CatERing.getInstance().getRecipeManager().getRecipe(rs.getInt("recipe_id"));
						t =  new Task(r);
						t.setEstimateTime(rs.getInt("preparation_id"));
					}else{
						Preparation p = CatERing.getInstance().getRecipeManager().getPreparation(rs.getInt("preparation_id"));
						t =  new Task(p);
						t.setEstimateTime(rs.getInt("preparation_id"));
					}
					t.setId(rs.getInt("id"));
					t.setCompleted(rs.getBoolean("completed"));
					t.setQuantity(rs.getString("quantity"));
					t.setEstimateTime(rs.getInt("estimateTime"));
					t.setCook(rs.getInt("cook_id"));
					t.setKitchenShift(rs.getInt("kitchenShift_id"));
					tasks.add(t);
				}while (rs.next());
			}
		});
		return tasks;
	}

	private void setKitchenShift(int kitchenShift_id) {
		List<WorkShift> shifts = CatERing.getInstance().getWorkshitMgr().getCalendarr().getWorkshifts();
		for (WorkShift s: shifts) {//prendo il turno che ha id = kitchenShift_id
			if (s.getId()==kitchenShift_id){
				setKitchenShift(s);
			}
		}
	}

	private void setCook(int cook_id) {
		String select = "SELECT * FROM catering.Users WHERE id = "+ cook_id;
		User cook = new User();
		PersistenceManager.executeQuery(select, new ResultHandler() {
			@Override
			public void handle(ResultSet rs) throws SQLException {
				cook.setId(cook_id);
				cook.setUsername(rs.getString("username"));
			}
		});
		if(cook.getId()!=0)
			this.cook=cook;
		else
			this.cook = null;
	}


	public static void removeTask(SummarySheet currentSheet, Task task) {
		String taskDelete = "DELETE FROM catering.Tasks WHERE id = "+task.id;
		PersistenceManager.executeUpdate(taskDelete);
	}

	//dato un id di un summarySheet elimino tutti i task relativi
	public static void deleteRelatedTasks(int id) {
		String taskDelete = "DELETE FROM catering.Tasks WHERE summary_sheet_id = "+id;
		PersistenceManager.executeUpdate(taskDelete);

	}

	@Override
	public String toString() {
		String ks = (kitchenShift!=null) ? kitchenShift.toString() : "non assegnato";
		String c = (cook!=null) ? cook.toString() : "non assegnato";
		String p = (procedure!=null) ? procedure.toString() : "non assegnata";
		return "-Procedura: " + p + '\n'+
				"-Turno: " + ks + '\n'+
				"-Assegnato a: " + c + '\n'+
				"-Completato: " + completed +'\n'+
				"-Quantità: " + quantity +'\n'+
				"-Tempo Stimato: " + estimateTime;
	}

	public int getId() {
		return id;
	}

	public void updateTask(SummarySheet currentSheet, Task task) {
		String upd="";
		int cookId = (task.getCook()!=null) ? task.getCook().getId() : 0;
		int kitchenShiftId = (task.getKitchenShift()!=null) ? task.getKitchenShift().getId() : 0;
		if(task.procedure instanceof Recipe){
			 upd = "UPDATE catering.Tasks SET completed = "+task.isCompleted()+", quantity = '"+task.quantity+"', " +
					"estimateTime = "+task.getEstimateTime()+", cook_id = "+cookId+", kitchenShift_id = "+kitchenShiftId+", " +
					"recipe_id = " +((Recipe) task.procedure).getId()+ ", preparation_id=0 WHERE id = "+task.getId();
		}else
		{
			 upd = "UPDATE catering.Tasks SET completed = "+task.isCompleted()+", quantity = '"+task.quantity+"', " +
					"estimateTime = "+task.getEstimateTime()+", cook_id = "+cookId+", kitchenShift_id = "+kitchenShiftId+", " +
					"preparation_id = " +((Preparation) task.procedure).getId()+", recipe_id=0 WHERE id = "+task.getId();
		}


		PersistenceManager.executeUpdate(upd);

	}
    public static ObservableList<Task> getTasks(WorkShift shift){
        String select = "SELECT * FROM catering.Tasks WHERE kitchenShift_id="+shift.getId();
        ObservableList<Task> tasks = FXCollections.observableArrayList();
        PersistenceManager.executeQuery(select, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                List<Recipe> allRecipes = CatERing.getInstance().getRecipeManager().getRecipes();//prendo tutte le ricette/preparazioni
                List<Preparation> allPeparations = CatERing.getInstance().getRecipeManager().getPreparations();

                do {
                    //int procedureId = (rs.getInt("recipe_id")!= 0) ? rs.getInt("recipe_id") : rs.getInt("preparation_id");
                    Task t;
                    if (rs.getInt("recipe_id")!=0){//se il task da creare è legato ad una ricetta
                        Recipe r = CatERing.getInstance().getRecipeManager().getRecipe(rs.getInt("recipe_id"));
                        t =  new Task(r);
                        t.setEstimateTime(rs.getInt("preparation_id"));
                    }else{
                        Preparation p = CatERing.getInstance().getRecipeManager().getPreparation(rs.getInt("preparation_id"));
                        t =  new Task(p);
                        t.setEstimateTime(rs.getInt("preparation_id"));
                    }
                    t.setId(rs.getInt("id"));
                    t.setCompleted(rs.getBoolean("completed"));
                    t.setQuantity(rs.getString("quantity"));
                    t.setEstimateTime(rs.getInt("estimateTime"));
                    t.setCook(rs.getInt("cook_id"));
                    t.setKitchenShift(rs.getInt("kitchenShift_id"));
                    tasks.add(t);
                }while (rs.next());
            }
        });
        return tasks;
    }

    public void nullifyAssigment(SummarySheet currentSheet, Task task) {
		String upd = "UPDATE catering.Tasks SET completed = "+task.isCompleted()+", quantity = '"+task.quantity+"', estimateTime = "+task.getEstimateTime()+", cook_id = "+0+", kitchenShift_id = "+0+" WHERE id = "+task.getId()+"";

		PersistenceManager.executeUpdate(upd);
	}
}
