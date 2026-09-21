package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView txtRecipeDetailName;
    private TextView txtRecipeIngredients;
    private TextView txtRecipeInstructions;

    private Button btnBackFromRecipe;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_recipe_detail
        );

        txtRecipeDetailName =
                findViewById(
                        R.id.txtRecipeDetailName
                );

        txtRecipeIngredients =
                findViewById(
                        R.id.txtRecipeIngredients
                );

        txtRecipeInstructions =
                findViewById(
                        R.id.txtRecipeInstructions
                );

        btnBackFromRecipe =
                findViewById(
                        R.id.btnBackFromRecipe
                );

        databaseHelper =
                new DatabaseHelper(this);

        int recipeId =
                getIntent().getIntExtra(
                        "recipe_id",
                        -1
                );

        if (recipeId != -1) {

            loadRecipe(recipeId);

        } else {

            finish();
        }

        btnBackFromRecipe.setOnClickListener(v ->
                finish()
        );
    }

    private void loadRecipe(int recipeId) {

        ArrayList<Recipe> recipes =
                databaseHelper.getAllRecipes();

        for (Recipe recipe : recipes) {

            if (recipe.getId() == recipeId) {

                txtRecipeDetailName.setText(
                        recipe.getName()
                );

                txtRecipeInstructions.setText(
                        recipe.getInstructions()
                );

                loadIngredients(recipeId);

                break;
            }
        }
    }

    private void loadIngredients(int recipeId) {

        ArrayList<RecipeIngredient> ingredients =
                databaseHelper.getRecipeIngredients(
                        recipeId
                );

        StringBuilder ingredientText =
                new StringBuilder();

        for (RecipeIngredient ingredient :
                ingredients) {

            ingredientText.append("• ")
                    .append(
                            ingredient.getIngredientName()
                    )
                    .append(" - ")
                    .append(
                            ingredient.getQuantity()
                    )
                    .append(" ")
                    .append(
                            ingredient.getUnit()
                    )
                    .append("\n");
        }

        txtRecipeIngredients.setText(
                ingredientText.toString()
        );
    }
}