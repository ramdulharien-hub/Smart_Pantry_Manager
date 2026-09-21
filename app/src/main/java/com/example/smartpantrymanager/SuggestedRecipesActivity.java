package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SuggestedRecipesActivity
        extends AppCompatActivity
        implements RecipeAdapter.OnRecipeClickListener {

    private RecyclerView recyclerRecipes;
    private TextView txtNoRecipes;
    private Button btnBack;

    private DatabaseHelper databaseHelper;
    private RecipeAdapter recipeAdapter;

    private ArrayList<Recipe> suggestedRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_suggested_recipes
        );

        recyclerRecipes =
                findViewById(R.id.recyclerRecipes);

        txtNoRecipes =
                findViewById(R.id.txtNoRecipes);

        btnBack =
                findViewById(R.id.btnBack);

        databaseHelper =
                new DatabaseHelper(this);

        recyclerRecipes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadSuggestedRecipes();

        btnBack.setOnClickListener(v ->
                finish()
        );
    }

    private void loadSuggestedRecipes() {

        ArrayList<Recipe> allRecipes =
                databaseHelper.getAllRecipes();

        suggestedRecipes =
                new ArrayList<>();

        for (Recipe recipe : allRecipes) {

            if (canMakeRecipe(recipe)) {

                suggestedRecipes.add(recipe);
            }
        }

        recipeAdapter =
                new RecipeAdapter(
                        suggestedRecipes,
                        this
                );

        recyclerRecipes.setAdapter(
                recipeAdapter
        );

        if (suggestedRecipes.isEmpty()) {

            txtNoRecipes.setVisibility(
                    TextView.VISIBLE
            );

        } else {

            txtNoRecipes.setVisibility(
                    TextView.GONE
            );
        }
    }

    private boolean canMakeRecipe(Recipe recipe) {

        ArrayList<RecipeIngredient> requiredIngredients =
                databaseHelper.getRecipeIngredients(
                        recipe.getId()
                );

        ArrayList<PantryItem> pantryItems =
                databaseHelper.getAllPantryItems();

        for (RecipeIngredient required :
                requiredIngredients) {

            boolean ingredientFound = false;

            for (PantryItem pantry :
                    pantryItems) {

                String pantryName =
                        normalizeIngredientName(
                                pantry.getName()
                        );

                String requiredName =
                        normalizeIngredientName(
                                required.getIngredientName()
                        );

                if (pantryName.equals(requiredName)) {

                    double pantryQuantity =
                            convertToBaseUnit(
                                    pantry.getQuantity(),
                                    pantry.getUnit()
                            );

                    double requiredQuantity =
                            convertToBaseUnit(
                                    required.getQuantity(),
                                    required.getUnit()
                            );

                    if (pantryQuantity >= requiredQuantity) {

                        ingredientFound = true;
                        break;
                    }
                }
            }

            if (!ingredientFound) {
                return false;
            }
        }

        return true;
    }

    private String normalizeIngredientName(String name) {

        if (name == null) {
            return "";
        }

        String result =
                name.trim().toLowerCase();

        if (result.endsWith("ies")) {

            result =
                    result.substring(
                            0,
                            result.length() - 3
                    ) + "y";

        } else if (result.endsWith("oes")) {

            result =
                    result.substring(
                            0,
                            result.length() - 2
                    );

        } else if (result.endsWith("s")
                && !result.endsWith("ss")) {

            result =
                    result.substring(
                            0,
                            result.length() - 1
                    );
        }

        return result;
    }

    private double convertToBaseUnit(
            double quantity,
            String unit) {

        if (unit == null) {
            return quantity;
        }

        String normalizedUnit =
                unit.trim().toLowerCase();

        switch (normalizedUnit) {

            case "kg":
            case "kilogram":
            case "kilograms":
                return quantity * 1000;

            case "g":
            case "gram":
            case "grams":
                return quantity;

            case "l":
            case "liter":
            case "liters":
            case "litre":
            case "litres":
                return quantity * 1000;

            case "ml":
            case "milliliter":
            case "milliliters":
            case "millilitre":
            case "millilitres":
                return quantity;

            case "piece":
            case "pieces":
            case "pc":
            case "pcs":
                return quantity;

            default:
                return quantity;
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe) {

        Intent intent =
                new Intent(
                        SuggestedRecipesActivity.this,
                        RecipeDetailActivity.class
                );

        intent.putExtra(
                "recipe_id",
                recipe.getId()
        );

        startActivity(intent);
    }
}