package com.example.smartpantrymanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import java.util.Calendar;

/**
 * Activity for Adding a new pantry ingredient or Editing an existing ingredient.
 * Includes ToggleButton edit mode, DatePickerDialog, TextWatcher events, and Input Validation.
 * Modeled directly on MainActivity (Chapters 3 & 5) in the MAD700 Learner Guide.
 */
public class MainActivity extends AppCompatActivity implements DatePickerDialog.SaveDateListener {

    private Ingredient currentIngredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPantryButton();
        initAddButton();
        initRecipesButton();
        initSettingsButton();

        initToggleButton();
        initChangeDateButton();
        initTextChangedEvents();
        initSaveButton();

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("ingredientId")) {
            initIngredient(extras.getInt("ingredientId"));
            ToggleButton editToggle = findViewById(R.id.toggleButtonEdit);
            editToggle.setChecked(false);
            setForEditing(false);
        } else {
            currentIngredient = new Ingredient();
            TextView expiryView = findViewById(R.id.textExpiryDate);
            expiryView.setText(DateFormat.format("dd/MM/yyyy", currentIngredient.getExpiryDate()));
            ToggleButton editToggle = findViewById(R.id.toggleButtonEdit);
            editToggle.setChecked(true);
            setForEditing(true);
        }
    }

    private void initIngredient(int id) {
        PantryDataSource ds = new PantryDataSource(MainActivity.this);
        try {
            ds.open();
            currentIngredient = ds.getSpecificIngredient(id);
            ds.close();
        } catch (Exception e) {
            Toast.makeText(this, "Load Ingredient Failed", Toast.LENGTH_LONG).show();
        }

        EditText editName = findViewById(R.id.editIngredientName);
        EditText editQty = findViewById(R.id.editQuantity);
        Spinner spinnerUnit = findViewById(R.id.spinnerUnit);
        Spinner spinnerCategory = findViewById(R.id.spinnerCategory);
        TextView textExpiry = findViewById(R.id.textExpiryDate);

        editName.setText(currentIngredient.getIngredientName());
        editQty.setText(String.valueOf(currentIngredient.getQuantity()));
        textExpiry.setText(DateFormat.format("dd/MM/yyyy", currentIngredient.getExpiryDate().getTimeInMillis()).toString());

        // Select matching unit in Spinner
        String[] units = getResources().getStringArray(R.array.units_array);
        for (int i = 0; i < units.length; i++) {
            if (units[i].equalsIgnoreCase(currentIngredient.getUnit())) {
                spinnerUnit.setSelection(i);
                break;
            }
        }

        // Select matching category in Spinner
        String[] categories = getResources().getStringArray(R.array.categories_array);
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equalsIgnoreCase(currentIngredient.getCategory())) {
                spinnerCategory.setSelection(i);
                break;
            }
        }
    }

    private void initToggleButton() {
        final ToggleButton editToggle = findViewById(R.id.toggleButtonEdit);
        editToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setForEditing(editToggle.isChecked());
            }
        });
    }

    private void setForEditing(boolean enabled) {
        EditText editName = findViewById(R.id.editIngredientName);
        EditText editQty = findViewById(R.id.editQuantity);
        Spinner spinnerUnit = findViewById(R.id.spinnerUnit);
        Spinner spinnerCategory = findViewById(R.id.spinnerCategory);
        Button buttonChange = findViewById(R.id.btnChangeExpiry);
        Button buttonSave = findViewById(R.id.buttonSave);

        editName.setEnabled(enabled);
        editQty.setEnabled(enabled);
        spinnerUnit.setEnabled(enabled);
        spinnerCategory.setEnabled(enabled);
        buttonChange.setEnabled(enabled);
        buttonSave.setEnabled(enabled);

        if (enabled) {
            editName.requestFocus();
        } else {
            ScrollView s = findViewById(R.id.scrollView);
            s.fullScroll(ScrollView.FOCUS_UP);
        }
    }

    private void initChangeDateButton() {
        Button changeDate = findViewById(R.id.btnChangeExpiry);
        changeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                DatePickerDialog datePickerDialog = new DatePickerDialog();
                datePickerDialog.show(fm, "DatePick");
            }
        });
    }

    @Override
    public void didFinishDatePickerDialog(Calendar selectedTime) {
        TextView expiryDay = findViewById(R.id.textExpiryDate);
        expiryDay.setText(DateFormat.format("dd/MM/yyyy", selectedTime));
        currentIngredient.setExpiryDate(selectedTime);
    }

    private void initTextChangedEvents() {
        final EditText etIngredientName = findViewById(R.id.editIngredientName);
        etIngredientName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentIngredient.setIngredientName(etIngredientName.getText().toString().trim());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Auto-generated method stub
            }
        });

        final EditText etQuantity = findViewById(R.id.editQuantity);
        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String val = etQuantity.getText().toString().trim();
                if (!val.isEmpty()) {
                    try {
                        currentIngredient.setQuantity(Double.parseDouble(val));
                    } catch (NumberFormatException e) {
                        currentIngredient.setQuantity(0.0);
                    }
                } else {
                    currentIngredient.setQuantity(0.0);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        final Spinner spinnerUnit = findViewById(R.id.spinnerUnit);
        spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (currentIngredient != null) {
                    currentIngredient.setUnit(parent.getItemAtPosition(position).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final Spinner spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (currentIngredient != null) {
                    currentIngredient.setCategory(parent.getItemAtPosition(position).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Validates user input before saving (Section 3.1 Core Requirement) and persists to SQLite.
     */
    private void initSaveButton() {
        Button saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editName = findViewById(R.id.editIngredientName);
                EditText editQty = findViewById(R.id.editQuantity);

                String nameInput = editName.getText().toString().trim();
                String qtyInput = editQty.getText().toString().trim();

                // Input Validation Check 1: Ingredient name must not be empty
                if (nameInput.isEmpty()) {
                    editName.setError("Ingredient name is required (e.g. Tomatoes)");
                    editName.requestFocus();
                    Toast.makeText(MainActivity.this, "Please enter an ingredient name.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Input Validation Check 2: Quantity must be a valid positive number > 0
                double parsedQty = 0.0;
                try {
                    parsedQty = Double.parseDouble(qtyInput);
                } catch (NumberFormatException e) {
                    parsedQty = -1.0;
                }

                if (qtyInput.isEmpty() || parsedQty <= 0.0) {
                    editQty.setError("Enter a valid quantity greater than 0");
                    editQty.requestFocus();
                    Toast.makeText(MainActivity.this, "Quantity must be greater than 0.", Toast.LENGTH_SHORT).show();
                    return;
                }

                currentIngredient.setIngredientName(nameInput);
                currentIngredient.setQuantity(parsedQty);

                hideKeyboard();

                boolean wasSuccessful;
                PantryDataSource ds = new PantryDataSource(MainActivity.this);
                try {
                    ds.open();
                    if (currentIngredient.getIngredientID() == -1) {
                        wasSuccessful = ds.insertIngredient(currentIngredient);
                        if (wasSuccessful) {
                            int newId = ds.getLastIngredientId();
                            currentIngredient.setIngredientID(newId);
                        }
                    } else {
                        wasSuccessful = ds.updateIngredient(currentIngredient);
                    }
                    ds.close();
                } catch (Exception e) {
                    wasSuccessful = false;
                }

                if (wasSuccessful) {
                    ToggleButton editToggle = findViewById(R.id.toggleButtonEdit);
                    editToggle.setChecked(false);
                    setForEditing(false);
                    Toast.makeText(MainActivity.this, "Saved to Pantry!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error saving ingredient to database.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        EditText editName = findViewById(R.id.editIngredientName);
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
        EditText editQty = findViewById(R.id.editQuantity);
        imm.hideSoftInputFromWindow(editQty.getWindowToken(), 0);
    }

    private void initPantryButton() {
        ImageButton ibPantry = findViewById(R.id.imageButtonPantry);
        ibPantry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PantryListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initAddButton() {
        ImageButton ibAdd = findViewById(R.id.imageButtonAdd);
        ibAdd.setEnabled(false);
    }

    private void initRecipesButton() {
        ImageButton ibRecipes = findViewById(R.id.imageButtonRecipes);
        ibRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SuggestedRecipesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initSettingsButton() {
        ImageButton ibSettings = findViewById(R.id.imageButtonSettings);
        ibSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PantrySettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
