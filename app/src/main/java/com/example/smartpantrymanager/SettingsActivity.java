package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchDarkMode;
    private Button btnClearData;
    private Button btnBackSettings;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_settings
        );

        switchDarkMode =
                findViewById(R.id.switchDarkMode);

        btnClearData =
                findViewById(R.id.btnClearData);

        btnBackSettings =
                findViewById(R.id.btnBackSettings);

        preferences =
                getSharedPreferences(
                        "SmartPantrySettings",
                        MODE_PRIVATE
                );

        boolean darkMode =
                preferences.getBoolean(
                        "dark_mode",
                        false
                );

        switchDarkMode.setChecked(darkMode);

        switchDarkMode.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    preferences.edit()
                            .putBoolean(
                                    "dark_mode",
                                    isChecked
                            )
                            .apply();

                    if (isChecked) {

                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_YES
                        );

                    } else {

                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_NO
                        );
                    }
                }
        );

        btnClearData.setOnClickListener(v -> {

            new android.app.AlertDialog.Builder(
                    SettingsActivity.this
            )
                    .setTitle("Clear Pantry Data")
                    .setMessage(
                            "Are you sure you want to delete " +
                                    "all pantry ingredients?"
                    )
                    .setPositiveButton(
                            "Yes",
                            (dialog, which) -> {

                                DatabaseHelper databaseHelper =
                                        new DatabaseHelper(
                                                SettingsActivity.this
                                        );

                                databaseHelper
                                        .deleteAllPantryItems();

                                Toast.makeText(
                                        SettingsActivity.this,
                                        "Pantry data cleared",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                    )
                    .setNegativeButton(
                            "No",
                            null
                    )
                    .show();
        });

        btnBackSettings.setOnClickListener(v ->
                finish()
        );
    }
}