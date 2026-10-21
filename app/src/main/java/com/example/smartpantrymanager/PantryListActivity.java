package com.example.smartpantrymanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Default Launcher Activity displaying the user's current pantry items in a RecyclerView.
 * Modeled on ContactListActivity (Chapter 5, Listings 6.5, 6.8, 6.12, 6.15, 6.16) of the MAD700 Learner Guide.
 */
public class PantryListActivity extends AppCompatActivity {

    ArrayList<Ingredient> ingredients;
    IngredientAdapter ingredientAdapter;
    RecyclerView rvIngredients;

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            int ingredientId = ingredients.get(position).getIngredientID();
            Intent intent = new Intent(PantryListActivity.this, MainActivity.class);
            intent.putExtra("ingredientId", ingredientId);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);

        initPantryButton();
        initAddButton();
        initRecipesButton();
        initSettingsButton();

        initAddIngredientButton();
        initDeleteSwitch();
    }

    @Override
    public void onResume() {
        super.onResume();

        String sortBy = getSharedPreferences("SmartPantryPreferences",
                Context.MODE_PRIVATE).getString("sortfield", "ingredientname");
        String sortOrder = getSharedPreferences("SmartPantryPreferences",
                Context.MODE_PRIVATE).getString("sortorder", "ASC");
        boolean highlightExpiring = getSharedPreferences("SmartPantryPreferences",
                Context.MODE_PRIVATE).getBoolean("expiryalerts", true);

        PantryDataSource ds = new PantryDataSource(this);
        try {
            ds.open();
            ingredients = ds.getIngredients(sortBy, sortOrder);
            ds.close();

            rvIngredients = findViewById(R.id.rvIngredients);
            TextView textEmpty = findViewById(R.id.textEmptyPantry);
            TextView textCount = findViewById(R.id.textPantryCount);

            textCount.setText(ingredients.size() + (ingredients.size() == 1 ? " Item" : " Items"));

            if (ingredients.size() > 0) {
                rvIngredients.setVisibility(View.VISIBLE);
                textEmpty.setVisibility(View.GONE);

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                rvIngredients.setLayoutManager(layoutManager);
                ingredientAdapter = new IngredientAdapter(ingredients, this, highlightExpiring);
                ingredientAdapter.setOnItemClickListener(onItemClickListener);
                rvIngredients.setAdapter(ingredientAdapter);

                // Preserve delete switch state if toggled
                Switch s = findViewById(R.id.switchDelete);
                ingredientAdapter.setDelete(s.isChecked());
            } else {
                rvIngredients.setVisibility(View.GONE);
                textEmpty.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error retrieving pantry ingredients", Toast.LENGTH_LONG).show();
        }
    }

    private void initAddIngredientButton() {
        Button newIngredient = findViewById(R.id.buttonAddIngredient);
        newIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PantryListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initDeleteSwitch() {
        Switch s = findViewById(R.id.switchDelete);
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (ingredientAdapter != null) {
                    Boolean status = compoundButton.isChecked();
                    ingredientAdapter.setDelete(status);
                    ingredientAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void initPantryButton() {
        ImageButton ibPantry = findViewById(R.id.imageButtonPantry);
        ibPantry.setEnabled(false);
    }

    private void initAddButton() {
        ImageButton ibAdd = findViewById(R.id.imageButtonAdd);
        ibAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PantryListActivity.this, MainActivity.class);
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
                Intent intent = new Intent(PantryListActivity.this, SuggestedRecipesActivity.class);
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
                Intent intent = new Intent(PantryListActivity.this, PantrySettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
