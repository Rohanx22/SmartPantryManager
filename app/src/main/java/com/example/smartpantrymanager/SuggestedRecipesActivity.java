package com.example.smartpantrymanager;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Activity that runs the Section 2.3 Strict-Matching Rule against the user's current SQLite pantry
 * and displays ONLY recipes where 100% of required ingredients and quantities are present.
 * Also provides a separate toggle for the bonus "Almost There" (missing 1 ingredient) list.
 */
public class SuggestedRecipesActivity extends AppCompatActivity {

    private ArrayList<Recipe> strictMatchedRecipes;
    private ArrayList<Recipe> almostThereRecipes;
    private ArrayList<Recipe> currentlyDisplayedRecipes;
    private boolean showingStrictMode = true;

    private RecyclerView rvRecipes;
    private RecipeAdapter recipeAdapter;

    private View.OnClickListener onRecipeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            if (currentlyDisplayedRecipes != null && position >= 0 && position < currentlyDisplayedRecipes.size()) {
                int recipeId = currentlyDisplayedRecipes.get(position).getRecipeID();
                Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
                intent.putExtra("recipeId", recipeId);
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        initPantryButton();
        initAddButton();
        initRecipesButton();
        initSettingsButton();

        initModeButtons();
    }

    @Override
    public void onResume() {
        super.onResume();
        evaluateRecipesAgainstPantry();
        refreshRecipeListDisplay();
    }

    private void evaluateRecipesAgainstPantry() {
        PantryDataSource ds = new PantryDataSource(this);
        try {
            ds.open();
            ArrayList<Ingredient> currentPantry = ds.getIngredients("ingredientname", "ASC");
            ArrayList<Recipe> allRecipes = ds.getAllRecipes();
            ds.close();

            strictMatchedRecipes = RecipeMatcher.getStrictMatchedRecipes(allRecipes, currentPantry);
            almostThereRecipes = RecipeMatcher.getAlmostThereRecipes(allRecipes, currentPantry);

            Button btnStrict = findViewById(R.id.buttonShowStrict);
            Button btnAlmost = findViewById(R.id.buttonShowAlmost);
            btnStrict.setText("Strict Matches (" + strictMatchedRecipes.size() + ")");
            btnAlmost.setText("Almost There (" + almostThereRecipes.size() + ")");
        } catch (Exception e) {
            strictMatchedRecipes = new ArrayList<>();
            almostThereRecipes = new ArrayList<>();
            Toast.makeText(this, "Error matching recipes against pantry.", Toast.LENGTH_LONG).show();
        }
    }

    private void initModeButtons() {
        final Button btnStrict = findViewById(R.id.buttonShowStrict);
        final Button btnAlmost = findViewById(R.id.buttonShowAlmost);

        btnStrict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showingStrictMode = true;
                refreshRecipeListDisplay();
            }
        });

        btnAlmost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showingStrictMode = false;
                refreshRecipeListDisplay();
            }
        });
    }

    private void refreshRecipeListDisplay() {
        Button btnStrict = findViewById(R.id.buttonShowStrict);
        Button btnAlmost = findViewById(R.id.buttonShowAlmost);
        TextView textSummary = findViewById(R.id.textMatchSummary);
        TextView textEmpty = findViewById(R.id.textEmptyRecipes);
        rvRecipes = findViewById(R.id.rvSuggestedRecipes);

        if (showingStrictMode) {
            btnStrict.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2E7D32")));
            btnStrict.setTextColor(Color.WHITE);
            btnAlmost.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BDBDBD")));
            btnAlmost.setTextColor(Color.parseColor("#212121"));

            textSummary.setText("Strict-Matching Rule: Showing ONLY recipes where you have 100% of required ingredients.");
            currentlyDisplayedRecipes = strictMatchedRecipes;
            textEmpty.setText(R.string.no_recipes_match_message);
        } else {
            btnAlmost.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EF6C00")));
            btnAlmost.setTextColor(Color.WHITE);
            btnStrict.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BDBDBD")));
            btnStrict.setTextColor(Color.parseColor("#212121"));

            textSummary.setText("Almost There (Bonus View): Showing recipes missing only 1 ingredient.");
            currentlyDisplayedRecipes = almostThereRecipes;
            textEmpty.setText(R.string.no_almost_there_message);
        }

        if (currentlyDisplayedRecipes != null && currentlyDisplayedRecipes.size() > 0) {
            rvRecipes.setVisibility(View.VISIBLE);
            textEmpty.setVisibility(View.GONE);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            rvRecipes.setLayoutManager(layoutManager);
            recipeAdapter = new RecipeAdapter(currentlyDisplayedRecipes);
            recipeAdapter.setOnItemClickListener(onRecipeClickListener);
            rvRecipes.setAdapter(recipeAdapter);
        } else {
            rvRecipes.setVisibility(View.GONE);
            textEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void initPantryButton() {
        ImageButton ibPantry = findViewById(R.id.imageButtonPantry);
        ibPantry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SuggestedRecipesActivity.this, PantryListActivity.class);
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
                Intent intent = new Intent(SuggestedRecipesActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initRecipesButton() {
        ImageButton ibRecipes = findViewById(R.id.imageButtonRecipes);
        ibRecipes.setEnabled(false);
    }

    private void initSettingsButton() {
        ImageButton ibSettings = findViewById(R.id.imageButtonSettings);
        ibSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SuggestedRecipesActivity.this, PantrySettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
