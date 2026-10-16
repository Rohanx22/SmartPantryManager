package com.example.smartpantrymanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Settings Activity storing user preferences for sorting pantry items, sort order,
 * expiring-soon alerts, and unit display using SharedPreferences.
 * Modeled directly on ContactSettingsActivity (Chapter 3, Listings 5.1 & 5.2) in the MAD700 Learner Guide.
 */
public class PantrySettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_settings);

        initPantryButton();
        initAddButton();
        initRecipesButton();
        initSettingsButton();

        initSettings();
        initSortByClick();
        initSortOrderClick();
        initExpiryAlertSwitch();
        initUnitsPreferenceClick();
    }

    private void initSettings() {
        String sortBy = getSharedPreferences("SmartPantryPreferences",
                Context.MODE_PRIVATE).getString("sortfield", "ingredientname");
        String sortOrder = getSharedPreferences("SmartPantryPreferences",
                Context.MODE_PRIVATE).getString("sortorder", "ASC");
        boolean expiryAlerts = getSharedPreferences("SmartPantryPreferences",
                Context.MODE_PRIVATE).getBoolean("expiryalerts", true);
        String unitPref = getSharedPreferences("SmartPantryPreferences",
                Context.MODE_PRIVATE).getString("unitpref", "metric");

        RadioButton rbName = findViewById(R.id.radioSortName);
        RadioButton rbExpiry = findViewById(R.id.radioSortExpiry);
        RadioButton rbCategory = findViewById(R.id.radioSortCategory);

        if (sortBy.equalsIgnoreCase("ingredientname")) {
            rbName.setChecked(true);
        } else if (sortBy.equalsIgnoreCase("expirydate")) {
            rbExpiry.setChecked(true);
        } else {
            rbCategory.setChecked(true);
        }

        RadioButton rbAscending = findViewById(R.id.radioAscending);
        RadioButton rbDescending = findViewById(R.id.radioDescending);
        if (sortOrder.equalsIgnoreCase("ASC")) {
            rbAscending.setChecked(true);
        } else {
            rbDescending.setChecked(true);
        }

        Switch swExpiry = findViewById(R.id.switchExpiryAlerts);
        swExpiry.setChecked(expiryAlerts);

        RadioButton rbMetric = findViewById(R.id.radioMetric);
        RadioButton rbKitchen = findViewById(R.id.radioKitchen);
        if (unitPref.equalsIgnoreCase("metric")) {
            rbMetric.setChecked(true);
        } else {
            rbKitchen.setChecked(true);
        }
    }

    private void initSortByClick() {
        RadioGroup rgSortBy = findViewById(R.id.radioGroupSortBy);
        rgSortBy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                RadioButton rbName = findViewById(R.id.radioSortName);
                RadioButton rbExpiry = findViewById(R.id.radioSortExpiry);
                if (rbName.isChecked()) {
                    getSharedPreferences("SmartPantryPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortfield", "ingredientname").apply();
                } else if (rbExpiry.isChecked()) {
                    getSharedPreferences("SmartPantryPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortfield", "expirydate").apply();
                } else {
                    getSharedPreferences("SmartPantryPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortfield", "category").apply();
                }
            }
        });
    }

    private void initSortOrderClick() {
        RadioGroup rgSortOrder = findViewById(R.id.radioGroupSortOrder);
        rgSortOrder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                RadioButton rbAscending = findViewById(R.id.radioAscending);
                if (rbAscending.isChecked()) {
                    getSharedPreferences("SmartPantryPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortorder", "ASC").apply();
                } else {
                    getSharedPreferences("SmartPantryPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortorder", "DESC").apply();
                }
            }
        });
    }

    private void initExpiryAlertSwitch() {
        Switch swExpiry = findViewById(R.id.switchExpiryAlerts);
        swExpiry.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getSharedPreferences("SmartPantryPreferences",
                        Context.MODE_PRIVATE).edit()
                        .putBoolean("expiryalerts", isChecked).apply();
            }
        });
    }

    private void initUnitsPreferenceClick() {
        RadioGroup rgUnits = findViewById(R.id.radioGroupUnits);
        rgUnits.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rbMetric = findViewById(R.id.radioMetric);
                if (rbMetric.isChecked()) {
                    getSharedPreferences("SmartPantryPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("unitpref", "metric").apply();
                } else {
                    getSharedPreferences("SmartPantryPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("unitpref", "kitchen").apply();
                }
            }
        });
    }

    private void initPantryButton() {
        ImageButton ibPantry = findViewById(R.id.imageButtonPantry);
        ibPantry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PantrySettingsActivity.this, PantryListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initAddButton() {
        ImageButton ibAdd = findViewById(R.id.imageButtonAdd);
        ibAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PantrySettingsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initRecipesButton() {
        ImageButton ibRecipes = findViewById(R.id.imageButtonRecipes);
        ibRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PantrySettingsActivity.this, SuggestedRecipesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initSettingsButton() {
        ImageButton ibSettings = findViewById(R.id.imageButtonSettings);
        ibSettings.setEnabled(false);
    }
}
