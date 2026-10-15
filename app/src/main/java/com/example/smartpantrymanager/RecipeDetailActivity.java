package com.example.smartpantrymanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Activity displaying the full ingredient list, pantry availability check, and preparation steps
 * for a selected Recipe. Allows deducting used ingredients when cooked.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    private Recipe currentRecipe;
    private boolean canCookStrictly = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        initPantryButton();
        initAddButton();
        initRecipesButton();
        initSettingsButton();

        initBackButton();
        initCookButton();

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("recipeId")) {
            loadRecipeDetails(extras.getInt("recipeId"));
        } else {
            Toast.makeText(this, "No recipe selected.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadRecipeDetails(int recipeId) {
        PantryDataSource ds = new PantryDataSource(this);
        ArrayList<Ingredient> pantryItems = new ArrayList<>();
        try {
            ds.open();
            currentRecipe = ds.getSpecificRecipe(recipeId);
            pantryItems = ds.getIngredients("ingredientname", "ASC");
            ds.close();
        } catch (Exception e) {
            Toast.makeText(this, "Error loading recipe details.", Toast.LENGTH_LONG).show();
            return;
        }

        TextView tvName = findViewById(R.id.textDetailRecipeName);
        TextView tvPrep = findViewById(R.id.textDetailPrepTime);
        TextView tvStatus = findViewById(R.id.textDetailMatchStatus);
        TextView tvIngredients = findViewById(R.id.textDetailIngredientsList);
        TextView tvSteps = findViewById(R.id.textDetailSteps);
        Button btnCook = findViewById(R.id.buttonCookRecipe);

        tvName.setText(currentRecipe.getRecipeName());
        tvPrep.setText("Preparation Time: " + currentRecipe.getPrepTimeMinutes()
                + " mins  |  Servings: " + currentRecipe.getServings());

        StringBuilder ingBuilder = new StringBuilder();
        int missingCount = 0;

        for (RecipeIngredient req : currentRecipe.getRequiredIngredients()) {
            boolean hasEnough = RecipeMatcher.hasSufficientIngredient(req, pantryItems);
            double availBase = RecipeMatcher.getAvailableBaseQuantity(req, pantryItems);
            double availInReqUnit = RecipeMatcher.convertFromBaseQuantity(availBase, req.getRequiredUnit());

            if (hasEnough) {
                ingBuilder.append("✓  ").append(req.getFormattedRequirement())
                        .append(String.format("  (In Pantry: %.0f %s)
", availInReqUnit, req.getRequiredUnit()));
            } else {
                missingCount++;
                ingBuilder.append("✗  ").append(req.getFormattedRequirement())
                        .append(String.format("  (Missing - Have: %.0f %s)
", availInReqUnit, req.getRequiredUnit()));
            }
        }

        tvIngredients.setText(ingBuilder.toString().trim());
        tvSteps.setText(currentRecipe.getInstructions().replace("\n", "
"));

        if (missingCount == 0) {
            canCookStrictly = true;
            tvStatus.setText("Strict Match: You have 100% of the required ingredients in your pantry!");
            tvStatus.setTextColor(Color.parseColor("#2E7D32"));
            btnCook.setEnabled(true);
        } else {
            canCookStrictly = false;
            tvStatus.setText("Almost There: Missing " + missingCount + " required ingredient(s).");
            tvStatus.setTextColor(Color.parseColor("#EF6C00"));
            btnCook.setEnabled(false);
        }
    }

    private void initBackButton() {
        Button btnBack = findViewById(R.id.buttonBackToRecipes);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initCookButton() {
        Button btnCook = findViewById(R.id.buttonCookRecipe);
        btnCook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentRecipe != null && canCookStrictly) {
                    PantryDataSource ds = new PantryDataSource(RecipeDetailActivity.this);
                    try {
                        ds.open();
                        boolean updated = ds.deductRecipeFromPantry(currentRecipe);
                        ds.close();
                        if (updated) {
                            Toast.makeText(RecipeDetailActivity.this,
                                    "Cooked " + currentRecipe.getRecipeName() + "! Pantry quantities updated.",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(RecipeDetailActivity.this,
                                "Error updating pantry quantities.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void initPantryButton() {
        ImageButton ibPantry = findViewById(R.id.imageButtonPantry);
        ibPantry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecipeDetailActivity.this, PantryListActivity.class);
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
                Intent intent = new Intent(RecipeDetailActivity.this, MainActivity.class);
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
                Intent intent = new Intent(RecipeDetailActivity.this, SuggestedRecipesActivity.class);
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
                Intent intent = new Intent(RecipeDetailActivity.this, PantrySettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
