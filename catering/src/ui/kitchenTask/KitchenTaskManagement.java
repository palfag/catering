package ui.kitchenTask;


import businesslogic.CatERing;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import ui.Main;

import java.io.IOException;

public class KitchenTaskManagement {

    @FXML
    BorderPane containerPane;

    Main mainPaneController;
    @FXML
    Label userLabel;

    @FXML
    BorderPane eventServiceListPane;

    @FXML
    EventServiceList eventServiceListPaneController;

    BorderPane sheetContentPane;
    SheetContent sheetContentPaneController;
    private boolean initialized = false;

    public void initialize() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sheet-content.fxml"));
        try {
            sheetContentPane = loader.load();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        sheetContentPaneController = loader.getController();
        sheetContentPaneController.setParent(this);

        if (CatERing.getInstance().getUserManager().getCurrentUser() != null) {
            String uname = CatERing.getInstance().getUserManager().getCurrentUser().getUserName();
            userLabel.setText(uname);
            // rifacciamo l'initialize perchè la prima volta la currentUser era null
            if (!initialized)
                eventServiceListPaneController.initialize(); //l'initialize deve essere fatta solo una volta, altrimenti i dialog non funzionano (si moltiplicano)
            initialized = true;
        }

        eventServiceListPaneController.setParent(this);
    }

    public void endKitchenTaskManagement() {
        mainPaneController.showStartPane();
    }

    public void setMainPaneController(Main main) {
        mainPaneController = main;
    }

    public void showEventServiceList() {
        containerPane.setCenter(eventServiceListPane);
    }


    public void showCurrentSummarySheet() {
        containerPane.getChildren().remove(eventServiceListPane);
        sheetContentPaneController.initialize();
        containerPane.setCenter(sheetContentPane);
    }
}
