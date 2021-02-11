package ui.kitchenTask;

import businesslogic.CatERing;
import businesslogic.event.EventInfo;
import businesslogic.recipe.Recipe;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;


public class RecipeBook {

    ObservableList<Recipe> recipes;

    @FXML
    ListView<Recipe> recipeListView;

    public void initialize(){
        recipes = CatERing.getInstance().getRecipeManager().getRecipes();
        recipeListView.setItems(recipes);
        recipeListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }


}
