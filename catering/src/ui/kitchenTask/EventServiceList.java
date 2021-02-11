package ui.kitchenTask;


import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.kitchenTask.KitchenTaskException;
import businesslogic.kitchenTask.SummarySheet;
import businesslogic.menu.Menu;
import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
import businesslogic.user.User;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.Optional;

public class EventServiceList {
    private KitchenTaskManagement kitchenTaskController;
    private ObservableList<EventInfo> eventListItem;

    @FXML
    ListView<EventInfo> eventList;

    @FXML
    ListView<ServiceInfo> serviceList;

    @FXML
    Pane itemsPane;
    @FXML
    GridPane centralPane;
    private Pane emptyPane;
    private boolean paneVisible = true;


    public void initialize()
    {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if(user != null) {
            eventListItem = CatERing.getInstance().getEventManager().getEventFromUser(user);
            eventList.setItems(eventListItem);
            eventList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            eventList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EventInfo>() {
                @Override
                public void changed(ObservableValue<? extends EventInfo> observableValue, EventInfo oldEventInfo, EventInfo newEventInfo) {
                    if (newEventInfo != null && newEventInfo != oldEventInfo) {
                        if (!paneVisible) {
                            centralPane.getChildren().remove(emptyPane);
                            centralPane.add(itemsPane, 1, 0);
                            paneVisible = true;
                        }
                        serviceList.setItems(newEventInfo.getServices());
                        CatERing.getInstance().getEventManager().setCurrentEvent(newEventInfo);
                    }
                }
            });

            serviceList.setItems(FXCollections.emptyObservableList());
            serviceList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            serviceList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ServiceInfo>() {
                @Override
                public void changed(ObservableValue<? extends ServiceInfo> observableValue, ServiceInfo oldServiceInfo, ServiceInfo newServiceInfo) {
                    if (newServiceInfo != null && newServiceInfo != oldServiceInfo) {
                        //Dialog per creare / aprire / rigenerare foglio
                        Alert alert = new Alert(Alert.AlertType.NONE);
                        alert.getDialogPane().getStylesheets().add("ui/View/style.css");
                        alert.setHeaderText(newServiceInfo.toString());
                        alert.setContentText("Scegli azione per Foglio Riepilogativo:");

                        // controlliamo se esiste un sheet per questo servizio, in base a questo cambiano i button da visualizzare
                        SummarySheet sheet = CatERing.getInstance().getKitchenMgr().loadSummarySheet(newServiceInfo);
                        ButtonType buttonTypeCreate = new ButtonType("Crea");
                        ButtonType buttonTypeOpen = new ButtonType("Apri");
                        ButtonType buttonTypeRestore = new ButtonType("Rigenera");
                        ButtonType buttonTypeCancel = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
                        if(sheet==null)
                            alert.getButtonTypes().setAll(buttonTypeCreate,buttonTypeCancel);
                        else
                            alert.getButtonTypes().setAll(buttonTypeOpen, buttonTypeRestore, buttonTypeCancel);
                        Optional<ButtonType> result = alert.showAndWait();
                        try {
                            if (result.get() == buttonTypeCreate) {
                                CatERing.getInstance().getKitchenMgr().createSheet(newServiceInfo);
                                kitchenTaskController.showCurrentSummarySheet();
                            } else if (result.get() == buttonTypeOpen) {
                                CatERing.getInstance().getKitchenMgr().openSheet(newServiceInfo);
                                kitchenTaskController.showCurrentSummarySheet();
                            } else if (result.get() == buttonTypeRestore) {
                                CatERing.getInstance().getKitchenMgr().restoreSheet(newServiceInfo);
                                kitchenTaskController.showCurrentSummarySheet();
                            } else {
                                // ... user chose CANCEL or closed the dialog
                            }
                        }
                        catch (UseCaseLogicException | KitchenTaskException ex)
                        { ex.printStackTrace(); }
                        Platform.runLater( ()-> {  serviceList.getSelectionModel().clearSelection();  });//a causa del fatto che si sta modificando l'indice selezionato mentre ci si trova all'interno del listener di modifiche per l'indice selezionato devo wrappare la clear dentro runLater
                    }
                }

            });
            emptyPane = new BorderPane();
            centralPane.getChildren().remove(itemsPane);
            centralPane.add(emptyPane, 1, 0);
            paneVisible = false;
        }
    }

    public void setParent(KitchenTaskManagement kitchenTaskManagement) {
        this.kitchenTaskController = kitchenTaskManagement;
    }

    public void exitButtonPressed(ActionEvent actionEvent) {
        kitchenTaskController.endKitchenTaskManagement();
    }
}
