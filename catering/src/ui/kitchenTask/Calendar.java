package ui.kitchenTask;


import businesslogic.CatERing;
import businesslogic.kitchenTask.Task;
import businesslogic.user.User;
import businesslogic.workShift.WorkShift;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

public class Calendar {
    ObservableList<WorkShift> shiftsList;
    @FXML
    ListView<WorkShift> shiftListView;

    @FXML
    ListView<Task> assignmentListView;

    @FXML
    ListView<User> availabilityListView;

    @FXML
    CheckBox checkComplete;

    public void initialize(){
        checkComplete.setDisable(true);
        shiftsList = CatERing.getInstance().getWorkshitMgr().getCalendarr().getWorkshifts();
        shiftListView.setItems(shiftsList);
        shiftListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        shiftListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<WorkShift>() {
            @Override
            public void changed(ObservableValue<? extends WorkShift> observableValue, WorkShift oldShift, WorkShift newShift) {
                if (newShift != null && newShift != oldShift) {
                    checkComplete.setDisable(false);
                    assignmentListView.setItems(CatERing.getInstance().getKitchenMgr().getTasks(newShift));
                    availabilityListView.setItems(newShift.getAvailability());
                    if (newShift.isComplete())
                        checkComplete.setSelected(true);
                    else
                        checkComplete.setSelected(false);
                    if(newShift.isPast())
                        checkComplete.setDisable(true);
                    else
                        checkComplete.setDisable(false);
                }
            }
        });

    }

    public void completeCheckPressed(ActionEvent actionEvent) {
        CatERing.getInstance().getWorkshitMgr().setComplete(shiftListView.getSelectionModel().getSelectedItem());
    }
}
