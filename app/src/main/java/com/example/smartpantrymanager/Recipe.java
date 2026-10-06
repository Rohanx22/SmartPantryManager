package com.example.smartpantrymanager;

import java.util.ArrayList;

/**
 * Model class representing a Recipe stored in the SQLite database.
 * Includes a list of required RecipeIngredient objects and preparation steps.
 */
public class Recipe {

    private int recipeID;
    private String recipeName;
    private int prepTimeMinutes;
    private int servings;
    private String instructions;
    private ArrayList<RecipeIngredient> requiredIngredients;

    // Transient fields populated by RecipeMatcher during evaluation
    private int missingIngredientCount;
    private String matchSummaryBadge;

    public Recipe() {
        recipeID = -1;
        requiredIngredients = new ArrayList<>();
        missingIngredientCount = 0;
        matchSummaryBadge = "";
    }

    public int getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(int recipeID) {
        this.recipeID = recipeID;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public int getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public void setPrepTimeMinutes(int prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public ArrayList<RecipeIngredient> getRequiredIngredients() {
        return requiredIngredients;
    }

    public void setRequiredIngredients(ArrayList<RecipeIngredient> requiredIngredients) {
        this.requiredIngredients = requiredIngredients;
    }

    public void addRequiredIngredient(RecipeIngredient ri) {
        this.requiredIngredients.add(ri);
    }

    public int getMissingIngredientCount() {
        return missingIngredientCount;
    }

    public void setMissingIngredientCount(int missingIngredientCount) {
        this.missingIngredientCount = missingIngredientCount;
    }

    public String getMatchSummaryBadge() {
        return matchSummaryBadge;
    }

    public void setMatchSummaryBadge(String matchSummaryBadge) {
        this.matchSummaryBadge = matchSummaryBadge;
    }
}
