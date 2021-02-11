package ui;

import businesslogic.CatERing;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import ui.kitchenTask.KitchenTaskManagement;
import ui.menu.MenuManagement;

import java.io.IOException;

public class Main {

    @FXML
    AnchorPane paneContainer;

    @FXML
    FlowPane startPane;

    @FXML
    Start startPaneController;

    BorderPane menuManagementPane;
    MenuManagement menuManagementPaneController;

    BorderPane kitchenTaskManagementPane;
    KitchenTaskManagement kitchenTaskManagementPaneController;



    public void initialize() {
        startPaneController.setParent(this);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("menu/menu-management.fxml"));
        FXMLLoader loaderKitchen = new FXMLLoader(getClass().getResource("kitchenTask/kitchenTask-management.fxml"));
        try {
            menuManagementPane = loader.load();
            menuManagementPaneController = loader.getController();
            menuManagementPaneController.setMainPaneController(this);

            kitchenTaskManagementPane = loaderKitchen.load();
            kitchenTaskManagementPaneController = loaderKitchen.getController();
            kitchenTaskManagementPaneController.setMainPaneController(this);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void startMenuManagement() {
        CatERing.getInstance().getUserManager().fakeLogin("Lidia");

        menuManagementPaneController.initialize();
        paneContainer.getChildren().remove(startPane);
        paneContainer.getChildren().add(menuManagementPane);
        AnchorPane.setTopAnchor(menuManagementPane, 0.0);
        AnchorPane.setBottomAnchor(menuManagementPane, 0.0);
        AnchorPane.setLeftAnchor(menuManagementPane, 0.0);
        AnchorPane.setRightAnchor(menuManagementPane, 0.0);

    }

    public void showStartPane() {
        startPaneController.initialize();
        paneContainer.getChildren().remove(menuManagementPane);
        paneContainer.getChildren().remove(kitchenTaskManagementPane);
        paneContainer.getChildren().add(startPane);
    }

    public void startKitchenTaskManagement() {
        CatERing.getInstance().getUserManager().fakeLogin("Lidia");
        if(CatERing.getInstance().getUserManager().getCurrentUser().isChef()) {
            kitchenTaskManagementPaneController.initialize();
            paneContainer.getChildren().remove(startPane);//tolgo lo start per sostituirlo
            paneContainer.getChildren().add(kitchenTaskManagementPane);
            AnchorPane.setTopAnchor(kitchenTaskManagementPane, 0.0);
            AnchorPane.setBottomAnchor(kitchenTaskManagementPane, 0.0);
            AnchorPane.setLeftAnchor(kitchenTaskManagementPane, 0.0);
            AnchorPane.setRightAnchor(kitchenTaskManagementPane, 0.0);
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().getStylesheets().add("ui/View/style.css");
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Non hai questi permessi");
            alert.setContentText(CatERing.getInstance().getUserManager().getCurrentUser().getUserName()+" non è uno Chef");

            alert.showAndWait();
        }

    }
}
