package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditIngredientActivity extends AppCompatActivity {

    private TextView txtFormTitle;

    private EditText edtIngredientName;
    private EditText edtQuantity;
    private EditText edtUnit;
    private EditText edtExpiryDate;

    private Button btnSaveIngredient;
    private Button btnCancel;

    private DatabaseHelper databaseHelper;

    private int ingredientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_add_edit_ingredient
        );

        // Connect XML components
        txtFormTitle =
                findViewById(R.id.txtFormTitle);

        edtIngredientName =
                findViewById(R.id.edtIngredientName);

        edtQuantity =
                findViewById(R.id.edtQuantity);

        edtUnit =
                findViewById(R.id.edtUnit);

        edtExpiryDate =
                findViewById(R.id.edtExpiryDate);

        btnSaveIngredient =
                findViewById(R.id.btnSaveIngredient);

        btnCancel =
                findViewById(R.id.btnCancel);

        // Create database helper
        databaseHelper =
                new DatabaseHelper(this);

        // Check if we are editing an existing ingredient
        ingredientId =
                getIntent().getIntExtra(
                        "ingredient_id",
                        -1
                );

        if (ingredientId != -1) {

            txtFormTitle.setText(
                    "Edit Ingredient"
            );

            loadIngredient();

        } else {

            txtFormTitle.setText(
                    "Add Ingredient"
            );
        }

        // Save button
        btnSaveIngredient.setOnClickListener(v ->
                saveIngredient()
        );

        // Cancel button
        btnCancel.setOnClickListener(v ->
                finish()
        );
    }

    private void saveIngredient() {

        String name =
                edtIngredientName.getText()
                        .toString()
                        .trim();

        String quantityText =
                edtQuantity.getText()
                        .toString()
                        .trim();

        String unit =
                edtUnit.getText()
                        .toString()
                        .trim();

        String expiryDate =
                edtExpiryDate.getText()
                        .toString()
                        .trim();

        // Validate ingredient name
        if (name.isEmpty()) {

            edtIngredientName.setError(
                    "Ingredient name is required"
            );

            edtIngredientName.requestFocus();

            return;
        }

        // Validate quantity
        if (quantityText.isEmpty()) {

            edtQuantity.setError(
                    "Quantity is required"
            );

            edtQuantity.requestFocus();

            return;
        }

        double quantity;

        try {

            quantity =
                    Double.parseDouble(quantityText);

        } catch (NumberFormatException e) {

            edtQuantity.setError(
                    "Enter a valid number"
            );

            edtQuantity.requestFocus();

            return;
        }

        // Quantity must be greater than zero
        if (quantity <= 0) {

            edtQuantity.setError(
                    "Quantity must be greater than zero"
            );

            edtQuantity.requestFocus();

            return;
        }

        // Validate unit
        if (unit.isEmpty()) {

            edtUnit.setError(
                    "Unit is required"
            );

            edtUnit.requestFocus();

            return;
        }

        // Create pantry item
        PantryItem item =
                new PantryItem(
                        name,
                        quantity,
                        unit,
                        expiryDate
                );

        if (ingredientId == -1) {

            // Add new ingredient
            long newId =
                    databaseHelper.addPantryItem(item);

            // Set the database ID on the item
            item.setId((int) newId);

            // Schedule expiry notification
            ExpiryNotificationHelper.scheduleExpiryAlert(
                    this,
                    item
            );

            Toast.makeText(
                    this,
                    "Ingredient added successfully",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            // Update existing ingredient
            item.setId(ingredientId);

            databaseHelper.updatePantryItem(item);

            // Cancel the old alert first
            ExpiryNotificationHelper.cancelExpiryAlert(
                    this,
                    ingredientId
            );

            // Schedule the updated expiry alert
            ExpiryNotificationHelper.scheduleExpiryAlert(
                    this,
                    item
            );

            Toast.makeText(
                    this,
                    "Ingredient updated successfully",
                    Toast.LENGTH_SHORT
            ).show();
        }

        finish();
    }

    private void loadIngredient() {

        java.util.ArrayList<PantryItem> pantryItems =
                databaseHelper.getAllPantryItems();

        for (PantryItem item : pantryItems) {

            if (item.getId() == ingredientId) {

                edtIngredientName.setText(
                        item.getName()
                );

                edtQuantity.setText(
                        String.valueOf(
                                item.getQuantity()
                        )
                );

                edtUnit.setText(
                        item.getUnit()
                );

                edtExpiryDate.setText(
                        item.getExpiryDate()
                );

                break;
            }
        }
    }
}