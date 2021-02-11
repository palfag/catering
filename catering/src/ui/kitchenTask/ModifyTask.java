package ui.kitchenTask;


import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.kitchenTask.KitchenTaskException;
import businesslogic.kitchenTask.Task;
import businesslogic.recipe.Preparation;
import businesslogic.recipe.Procedure;
import businesslogic.recipe.Recipe;
import businesslogic.user.User;
import businesslogic.workShift.WorkShift;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.Instant;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.ZoneId;
import java.util.Date;

public class ModifyTask {
    Stage stage;//per chiudersi
    SheetContent parentController;

    Task selectedTask;

    boolean modify=false;

    ObservableList<WorkShift> kitchenShifts;
    ObservableList<businesslogic.recipe.Procedure> procedures;

    @FXML
    CheckBox checkCompleted;

    @FXML
    TextField estimateTimeTextField;
    @FXML
    TextField quantityTextField;
    @FXML
    ComboBox<User> cooksListCombo;
    @FXML
    DatePicker datePicker;
    @FXML
    Button btnNullifyAssignment;
    @FXML
    Label cookLabel;
    @FXML
    ComboBox<Procedure> procedureCombo;
    @FXML
    Label timeLabel;
    @FXML
    Label quantityLabel;


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setParentController(SheetContent sheetContent) {
        this.parentController = sheetContent;
    }



    public void setSelectedTask(Task selectedItem) {
        this.selectedTask =  selectedItem;
        checkCompleted.setSelected(selectedTask.isCompleted());
        if (selectedTask.isCompleted()){
            btnNullifyAssignment.setDisable(true);
            procedureCombo.setDisable(true);
            datePicker.setDisable(true);
            hoursCombo.setDisable(true);
            cooksListCombo.setDisable(true);
            estimateTimeTextField.setDisable(true);
            quantityTextField.setDisable(true);
        }
    }








    @FXML
    ComboBox<WorkShift> hoursCombo;
    //quando scelgo la data parte questo metodo: seleziona tutti i turni in quella data e li aggiunge alla lista per la visualizzazione degli orari
    public void dateChose(ActionEvent actionEvent) {
        hoursCombo.setDisable(false);
        ObservableList<WorkShift> ksChosen = FXCollections.observableArrayList();
        for (WorkShift ks: kitchenShifts) {
            LocalDate localDate = datePicker.getValue();
            Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            Date date = Date.from(instant);
            if ((ks.getDate().getMonth()+1 == date.getMonth()+1) && (ks.getDate().getDate() == date.getDate())){
                ksChosen.add(ks);//aggiungo i turni con la stessa data di quello cliccato
            }
        }
        hoursCombo.setItems(ksChosen);
    }

    @FXML
    Label shiftLabel;
    //quando scelgo l'ora faccio questo metodo
    public void hoursChose(ActionEvent actionEvent) {//lo fa anche quando cambio la data dal datePicker
        //attivo la scelta del cuoco
        cooksListCombo.setDisable(false);
        setCookList(null);
        if(hoursCombo.getSelectionModel().getSelectedItem()!=null) {
            setCookList(CatERing.getInstance().getUserManager().getCooks(hoursCombo.getSelectionModel().getSelectedItem()));
            if(hoursCombo.getSelectionModel().getSelectedItem().isComplete())
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.getDialogPane().getStylesheets().add("ui/View/style.css");
                alert.setTitle("Avviso");
                alert.setHeaderText(null);
                alert.setContentText("Questo turno è segnato come completo, sarebbe meglio non sovraccaricare il turno di cucina");
                alert.showAndWait();
            }

        }
    }


    private void setCookList(ObservableList<User> cooks) {
        cooksListCombo.setItems(cooks);
    }

    public void setShiftCalendar(ObservableList<WorkShift> kitchenShifts) {
        this.kitchenShifts=kitchenShifts;
        datePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker datePicker) {
                return new DateCell() {
                    @Override public void updateItem(LocalDate item, boolean empty) {
                        setDisable(true);
                        super.updateItem(item, empty);
                        for (WorkShift s: kitchenShifts) {
                            LocalDate today = LocalDate.now();
                            if (MonthDay.from(item).equals(MonthDay.of(s.getDate().getMonth()+1, s.getDate().getDate())) && (item.getYear()== s.getDate().getYear()+1900)&& !(getStyleClass().contains("next-month") || getStyleClass().contains("previous-month"))){
                                setTooltip(new Tooltip("Questa data contiene turni di cucina"));
                                setDisable(false);
                                setDisable(item.compareTo(today) < 0 );//NOTA BENE: rendo non cliccabili le date passate
                            }


                        }
                    }
                };
            }
        });
    }

    public void saveButtonPressed(ActionEvent actionEvent) throws UseCaseLogicException, KitchenTaskException {
        boolean exception = false;
        selectedTask.setCompleted(checkCompleted.isSelected());
        CatERing.getInstance().getKitchenMgr().taskIsReady(selectedTask);
        if (!estimateTimeTextField.getText().equals("") && estimateTimeTextField.getText() != null){
            //prendo il TEMPO
            try {
                CatERing.getInstance().getKitchenMgr().providesTime(selectedTask, Integer.parseInt(estimateTimeTextField.getText()));
            }catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.getDialogPane().getStylesheets().add("ui/View/style.css");
                alert.setTitle("Attenzione");
                alert.setHeaderText("Input errato");
                alert.setContentText("Inserire un valore numerico!");
                alert.showAndWait();
                exception=!exception;
            }
        }
        if (!exception) {//se non è stata lanciata l'eccezione

            if (!quantityTextField.getText().equals("") && quantityTextField.getText() != null)//prendo la QUANTITA
                CatERing.getInstance().getKitchenMgr().providesQuantity(selectedTask, quantityTextField.getText());
            if (hoursCombo.getValue() != null && cooksListCombo.getValue() == null && !modify)
                CatERing.getInstance().getKitchenMgr().assignTask(selectedTask, hoursCombo.getValue());
            if (cooksListCombo.getValue() != null && !modify)
                CatERing.getInstance().getKitchenMgr().assignTask(selectedTask, hoursCombo.getValue(), cooksListCombo.getValue());
            if (procedureCombo.getValue() != null)//modifico la procedura
                CatERing.getInstance().getKitchenMgr().modifyAssignment(selectedTask, procedureCombo.getValue());
            if (modify) {//sto modificando
                if (hoursCombo.getValue() != null /*&& cooksListCombo.getValue() == null*/)//modifico solo il turno
                    CatERing.getInstance().getKitchenMgr().modifyAssignment(selectedTask, hoursCombo.getValue());
                if (cooksListCombo.getValue() != null)//modifico il cuoco
                    CatERing.getInstance().getKitchenMgr().modifyAssignment(selectedTask, cooksListCombo.getValue());
            }
            parentController.taskList.refresh();
            stage.close();
        }
    }

    @FXML
    Label procedureLabel;
    public void init() {
        //devo creare le procedure da mettere nella combo
        procedures= FXCollections.observableArrayList();
        ObservableList<Recipe> recipes = CatERing.getInstance().getRecipeManager().getRecipes();
        ObservableList<Preparation> preparations = CatERing.getInstance().getRecipeManager().getPreparations();
        procedures.addAll(recipes);
        procedures.addAll(preparations);
        procedureCombo.setItems(procedures);

        //ho bisogno di sapere se esiste l'assegnamento così da sapere de devo fare MODIFICA ASSEGNAMENTO che è diverso
        if (selectedTask.getCook()!=null){
            //se il cuoco è già stato scelto
            modify = true;//sto modificando l'assegnamento
        }

        procedureLabel.setText("Procedura: "+selectedTask.getProcedure().getName());
        if(selectedTask.getCook()!=null)
            cookLabel.setText("Cuoco: "+selectedTask.getCook().getUserName());
        else
            cookLabel.setText("Cuoco: non ancora assegnato");
        if(selectedTask.getKitchenShift()!=null)
            shiftLabel.setText("Turno: "+selectedTask.getKitchenShift().toString());
        else
            shiftLabel.setText("Turno: non ancora assegnato");
        if(selectedTask.getEstimateTime()!=0)
            timeLabel.setText("Tempo stimato: "+selectedTask.getEstimateTime());
        if(!selectedTask.getQuantity().equals("non fornita"))
            quantityLabel.setText("Quantità: "+selectedTask.getQuantity());
        if((selectedTask.getCook() != null || selectedTask.getKitchenShift() != null) && !selectedTask.isCompleted())
            btnNullifyAssignment.setDisable(false); // elimina Assegnamento si puù solo fare se abbiamo qualsosa da annullare!

    }

    @FXML
    public void nullifyAssignment(ActionEvent actionEvent)
    {
        try {
            CatERing.getInstance().getKitchenMgr().nullifyAssignment(selectedTask);
            btnNullifyAssignment.setDisable(true);
            procedureCombo.setDisable(true);
            procedureCombo.setItems(null);
            datePicker.setDisable(true);
            hoursCombo.setDisable(true);
            hoursCombo.setItems(null);
            cooksListCombo.setDisable(true);
            cooksListCombo.setItems(null);
            quantityTextField.setDisable(true);
            estimateTimeTextField.setDisable(true);
            init();
            parentController.taskList.refresh();

        } catch (UseCaseLogicException | KitchenTaskException e) {
            e.printStackTrace();
        }
    }

    public void exitButtonPressed(ActionEvent actionEvent) {
        stage.close();
    }

    public void completedCheck(ActionEvent actionEvent) {
        if(checkCompleted.isSelected()){
            btnNullifyAssignment.setDisable(true);
            procedureCombo.setDisable(true);
            procedureCombo.setItems(null);
            datePicker.setDisable(true);
            hoursCombo.setDisable(true);
            hoursCombo.setItems(null);
            cooksListCombo.setDisable(true);
            cooksListCombo.setItems(null);
        }else{
            if(!selectedTask.isCompleted())
                btnNullifyAssignment.setDisable(false);
            procedureCombo.setDisable(false);
            procedureCombo.setItems(procedures);
            datePicker.setDisable(false);
            hoursCombo.setDisable(false);
            cooksListCombo.setDisable(false);
            quantityTextField.setDisable(false);
            estimateTimeTextField.setDisable(false);
        }
    }
}
