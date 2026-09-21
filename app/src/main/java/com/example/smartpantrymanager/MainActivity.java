package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements PantryAdapter.OnPantryItemListener {

    private RecyclerView recyclerPantry;
    private TextView txtEmptyPantry;
    private Button btnAddIngredient;

    private Button btnSuggestedRecipes;

    private Button btnSettings;

    private DatabaseHelper databaseHelper;
    private PantryAdapter pantryAdapter;

    private ArrayList<PantryItem> pantryItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        android.content.SharedPreferences preferences =
                getSharedPreferences(
                        "SmartPantrySettings",
                        MODE_PRIVATE
                );

        boolean darkMode =
                preferences.getBoolean(
                        "dark_mode",
                        false
                );

        if (darkMode) {

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
            );

        } else {

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
            );
        }

        setContentView(R.layout.activity_main);

        // Connect XML components
        recyclerPantry = findViewById(R.id.recyclerPantry);
        txtEmptyPantry = findViewById(R.id.txtEmptyPantry);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnSuggestedRecipes =
                findViewById(R.id.btnSuggestedRecipes);
        btnSettings =
                findViewById(R.id.btnSettings);

        // Create database helper
        databaseHelper = new DatabaseHelper(this);

        // Set up RecyclerView
        recyclerPantry.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // Load pantry items
        loadPantryItems();
        btnAddIngredient.setOnClickListener(v -> {

            android.content.Intent intent =
                    new android.content.Intent(
                            MainActivity.this,
                            AddEditIngredientActivity.class
                    );

            startActivity(intent);
        });

        btnSuggestedRecipes.setOnClickListener(v -> {

            android.content.Intent intent =
                    new android.content.Intent(
                            MainActivity.this,
                            SuggestedRecipesActivity.class
                    );

            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {

            android.content.Intent intent =
                    new android.content.Intent(
                            MainActivity.this,
                            SettingsActivity.class
                    );

            startActivity(intent);
        });

    }

    private void loadPantryItems() {

        pantryItems = databaseHelper.getAllPantryItems();

        pantryAdapter = new PantryAdapter(
                pantryItems,
                this
        );

        recyclerPantry.setAdapter(pantryAdapter);

        // Show or hide empty message
        if (pantryItems.isEmpty()) {

            txtEmptyPantry.setVisibility(TextView.VISIBLE);

        } else {

            txtEmptyPantry.setVisibility(TextView.GONE);
        }
    }

    @Override
    public void onEdit(PantryItem item) {

        android.content.Intent intent =
                new android.content.Intent(
                        MainActivity.this,
                        AddEditIngredientActivity.class
                );

        intent.putExtra(
                "ingredient_id",
                item.getId()
        );

        startActivity(intent);
    }

    @Override
    public void onDelete(PantryItem item) {

        new android.app.AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage(
                        "Are you sure you want to delete "
                                + item.getName()
                                + "?"
                )
                .setPositiveButton("Yes", (dialog, which) -> {

                    databaseHelper.deletePantryItem(
                            item.getId()
                    );

                    loadPantryItems();

                    android.widget.Toast.makeText(
                            MainActivity.this,
                            "Ingredient deleted",
                            android.widget.Toast.LENGTH_SHORT
                    ).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh pantry whenever the screen becomes active
        if (databaseHelper != null) {
            loadPantryItems();
        }
    }
}