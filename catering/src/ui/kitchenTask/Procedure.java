package ui.kitchenTask;


import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.kitchenTask.KitchenTaskException;
import businesslogic.recipe.Preparation;
import businesslogic.recipe.Recipe;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;

public class Procedure {
    private ObservableList<businesslogic.recipe.Procedure> procedures;

    Stage stage;
    SheetContent parentController;

    @FXML
    private ListView<businesslogic.recipe.Procedure> procedureListView;


    public void initialize(){
        procedures = FXCollections.observableArrayList();
        ObservableList<Recipe> recipes = CatERing.getInstance().getRecipeManager().getRecipes();
        ObservableList<Preparation> preparations = CatERing.getInstance().getRecipeManager().getPreparations();

        procedures.addAll(recipes);
        procedures.addAll(preparations);
        procedureListView.setItems(procedures);
        procedureListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        procedureListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<businesslogic.recipe.Procedure>() {
            @Override
            public void changed(ObservableValue<? extends businesslogic.recipe.Procedure> observableValue, businesslogic.recipe.Procedure oldProcedure, businesslogic.recipe.Procedure newProcedure) {
                if (newProcedure != null && newProcedure != oldProcedure) {
                    try {
                        parentController.procedureSelected(newProcedure);
                    } catch (UseCaseLogicException | KitchenTaskException e) {
                        e.printStackTrace();
                    }
                    stage.close();//appena clicco su una procedura invio l'informazione al controller dell'altra schermata e chiudo questa
                }
            }
        });

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setParentController(SheetContent sheetContent) {
        this.parentController = sheetContent;
    }
}
