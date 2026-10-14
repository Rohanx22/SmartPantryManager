package com.example.smartpantrymanager;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Custom RecyclerView.Adapter for displaying Suggested Recipes (Strict Matches and Almost There).
 * Modeled on Chapter 5 RecyclerView adapter conventions in the MAD700 Learner Guide.
 */
public class RecipeAdapter extends RecyclerView.Adapter {

    private ArrayList<Recipe> recipeData;
    private View.OnClickListener mOnItemClickListener;

    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        public TextView textRecipeName;
        public TextView textRecipeBadge;
        public TextView textRecipeMeta;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            textRecipeName = itemView.findViewById(R.id.textRecipeName);
            textRecipeBadge = itemView.findViewById(R.id.textRecipeBadge);
            textRecipeMeta = itemView.findViewById(R.id.textRecipeMeta);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }

        public TextView getRecipeNameView() {
            return textRecipeName;
        }

        public TextView getRecipeBadgeView() {
            return textRecipeBadge;
        }

        public TextView getRecipeMetaView() {
            return textRecipeMeta;
        }
    }

    public RecipeAdapter(ArrayList<Recipe> arrayList) {
        recipeData = arrayList;
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_recipe, parent, false);
        return new RecipeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecipeViewHolder rvh = (RecipeViewHolder) holder;
        Recipe current = recipeData.get(position);

        rvh.getRecipeNameView().setText(current.getRecipeName());
        rvh.getRecipeBadgeView().setText(current.getMatchSummaryBadge());

        if (current.getMissingIngredientCount() == 0) {
            rvh.getRecipeBadgeView().setTextColor(Color.parseColor("#2E7D32"));
        } else {
            rvh.getRecipeBadgeView().setTextColor(Color.parseColor("#EF6C00"));
        }

        String meta = "Prep Time: " + current.getPrepTimeMinutes() + " mins  |  Servings: "
                + current.getServings() + "  |  Ingredients: " + current.getRequiredIngredients().size();
        rvh.getRecipeMetaView().setText(meta);
    }

    @Override
    public int getItemCount() {
        return recipeData.size();
    }
}
