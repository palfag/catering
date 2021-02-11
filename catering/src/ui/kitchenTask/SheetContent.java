package ui.kitchenTask;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.kitchenTask.KitchenTaskException;
import businesslogic.kitchenTask.SummarySheet;
import businesslogic.kitchenTask.Task;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.geometry.Pos.CENTER_RIGHT;

public class SheetContent {
	@FXML
	ListView<Task> taskList;

	@FXML
	Button upTaskButton;
	@FXML
	Button downTaskButton;

	@FXML
	Label titleLabel;

	@FXML
	Button modifyTaskBtn;
	@FXML
	Button removeTaskBtn;



	private KitchenTaskManagement kitchenTaskManagement;
	private ObservableList<Task> taskListItems;

	public void initialize() {
		SummarySheet sheet = CatERing.getInstance().getKitchenMgr().getCurrentSheet();
		if (sheet != null) {
			titleLabel.setText(sheet.getService().getName());
			taskListItems = sheet.getTasks();
			taskList.setItems(taskListItems);
			taskList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
			taskList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Task>() {
				@Override
				public void changed(ObservableValue<? extends Task> observableValue, Task oldTask, Task newTask) {
					if (newTask != null && newTask != oldTask) {
						modifyTaskBtn.setDisable(false);
						removeTaskBtn.setDisable(false);
						if(newTask.isCompleted())
							removeTaskBtn.setDisable(true);
						//inibisco eventualmente i bottoni up e down
//						int pos = taskList.getSelectionModel().getSelectedIndex();
//						upTaskButton.setDisable(pos <= 0);
//						downTaskButton.setDisable(pos >= (CatERing.getInstance().getKitchenMgr().getCurrentSheet().getTasksCount()-1));

					}
				}
			});
			taskList.setCellFactory((ListView<Task> param)   -> {
				return new ListCell<Task>() {
					@Override
					protected void updateItem(Task task, boolean empty) {
						super.updateItem(task, empty);

						if (task == null || empty) {
							setText(null);
							setGraphic(null); //se non faccio setGraphic(null) non pulisce la grafica
						} else {
							// Creo il layout per ogni cella
							HBox root = new HBox(10);
							root.setAlignment(CENTER_RIGHT);
							root.setPadding(new Insets(5, 10, 5, 10));

							// Dentro la radice metto una label con l'task
							Label taskLabel = new Label(task.toString());
							root.getChildren().add(taskLabel);
							taskLabel.setTextFill(Color.BLACK);

							// Serve per separare il bottone dalla label che mostra le task
							Region region = new Region();
							HBox.setHgrow(region, Priority.ALWAYS);
							root.getChildren().add(region);

							// creo il bottone e definisco l'azione onAction
							Button btnUp = new Button("^");
							Button btnDwn = new Button("v");
							btnUp.setOnAction(event -> {
								upTaskPressed(event);
							});
							btnDwn.setOnAction(event -> {
								downTaskPressed(event);
							});
							if(taskList.getSelectionModel().getSelectedItem() == task) {
								int pos = taskList.getSelectionModel().getSelectedIndex();
								root.getChildren().add(btnUp);
								root.getChildren().add(btnDwn);
								btnUp.setDisable(pos <= 0);
								btnDwn.setDisable(pos >= (CatERing.getInstance().getKitchenMgr().getCurrentSheet().getTasksCount()-1));
							}
							// settiamo nella cella il nuovo layout
							setText(null);
							setGraphic(root);
						}
					}
				};
			});

		}
	}


	public void setParent(KitchenTaskManagement kitchenTaskManagement) {
		this.kitchenTaskManagement = kitchenTaskManagement;
	}

	@FXML
	public void exitSheetContent()
	{
		kitchenTaskManagement.showEventServiceList();
		modifyTaskBtn.setDisable(true);
		removeTaskBtn.setDisable(true);
	}


	//quando clocco sul [+] mi spunta una lista di procedure tra cui scegliere
	@FXML
	public void addProcedurePressed(ActionEvent actionEvent) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("procedure.fxml"));
		Parent root = (Parent) loader.load();
		Procedure procedure = loader.getController();

		Stage stage =  new Stage();
		stage.setTitle("Scegli una Procedura");
		Scene scene = new Scene(root);
		scene.getStylesheets().add("ui/View/style.css");
		stage.setScene(scene);
		procedure.setStage(stage);//passo al controller della vista delle procedure lo stage su cui è creato per potersi autodistruggere
		procedure.setParentController(this);//passo al controller della vista delle procedure il padre
		stage.show();
	}








	@FXML
	public void removeTaskButtonPressed(ActionEvent actionEvent) throws KitchenTaskException, UseCaseLogicException {
		CatERing.getInstance().getKitchenMgr().removeTask(taskList.getSelectionModel().getSelectedItem());
	}
	public void upTaskPressed(ActionEvent actionEvent) {
		this.changeItemPosition(-1);
	}

	public void downTaskPressed(ActionEvent actionEvent) {
		this.changeItemPosition(+1);
	}




	@FXML
	public void getRecipeBook(ActionEvent actionEvent) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("recipe_book.fxml"));
		Parent root = (Parent) loader.load();
		Stage stage =  new Stage();
		stage.setTitle("Ricettario");
		Scene scene = new Scene(root);
		scene.getStylesheets().add("ui/View/style.css");
		stage.setScene(scene);
		stage.show();

	}
	public void openCalendarButtonPressed(ActionEvent actionEvent) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("calendar.fxml"));
		Parent root = (Parent) loader.load();
		Stage stage =  new Stage();
		stage.setTitle("Tabellone");
		Scene scene = new Scene(root);
		scene.getStylesheets().add("ui/View/style.css");
		stage.setScene(scene);
		stage.show();
	}

	public void procedureSelected(businesslogic.recipe.Procedure newProcedure) throws UseCaseLogicException,KitchenTaskException {
		CatERing.getInstance().getKitchenMgr().insertProcedure(newProcedure);
	}

	private void changeItemPosition(int change) {
		int newpos = taskList.getSelectionModel().getSelectedIndex() + change;
		Task task = taskList.getSelectionModel().getSelectedItem();
		try {
			CatERing.getInstance().getKitchenMgr().moveTask(task, newpos);
			taskList.refresh();
			taskList.getSelectionModel().select(newpos);
		} catch (UseCaseLogicException ex) {
			ex.printStackTrace();
		}
	}

	@FXML
	public void modifyTaskButtonPressed(ActionEvent actionEvent) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("modify_task.fxml"));
		Parent root = (Parent) loader.load();
		ModifyTask modifyTask = loader.getController();

		Stage stage =  new Stage();
		stage.setTitle("Modifica compito");
		Scene scene = new Scene(root,650,600);
		scene.getStylesheets().add("ui/View/style.css");
		stage.setScene(scene);
		modifyTask.setStage(stage);//passo al controller della vista delle modifyTask lo stage su cui è creato per potersi autodistruggere
		modifyTask.setParentController(this);//passo al controller della vista delle procedure il padre
		modifyTask.setSelectedTask(taskList.getSelectionModel().getSelectedItem());
		modifyTask.setShiftCalendar(CatERing.getInstance().getWorkshitMgr().getCalendarr().getKitchenShifts());
		modifyTask.init();
		stage.show();
	}
}
