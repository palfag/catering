package businesslogic.recipe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RecipeManager {

    public RecipeManager() {
        Recipe.loadAllRecipes();
        Preparation.loadAllPreparations();
    }

    public ObservableList<Recipe> getRecipes() {
        return FXCollections.unmodifiableObservableList(Recipe.getAllRecipes());
    }

    public ObservableList<Preparation> getPreparations() {
        return FXCollections.unmodifiableObservableList(Preparation.getAllPreparations());
    }

    public Recipe getRecipe(int recipe_id) {
        for (Recipe r: getRecipes()) {
            if (r.getId()==recipe_id) return r;
        }
        return null;
    }

    public Preparation getPreparation(int preparation_id) {
        for (Preparation p: getPreparations()) {
            if (p.getId()==preparation_id) return p;
        }
        return null;
    }
}
