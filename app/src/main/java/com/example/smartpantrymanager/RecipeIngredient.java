package com.example.smartpantrymanager;

/**
 * Model class representing a single required ingredient and quantity for a Recipe.
 */
public class RecipeIngredient {

    private int recipeIngredientID;
    private int recipeID;
    private String ingredientName;
    private double requiredQuantity;
    private String requiredUnit;

    public RecipeIngredient() {
        recipeIngredientID = -1;
        recipeID = -1;
    }

    public RecipeIngredient(String name, double quantity, String unit) {
        this.recipeIngredientID = -1;
        this.recipeID = -1;
        this.ingredientName = name;
        this.requiredQuantity = quantity;
        this.requiredUnit = unit;
    }

    public int getRecipeIngredientID() {
        return recipeIngredientID;
    }

    public void setRecipeIngredientID(int id) {
        this.recipeIngredientID = id;
    }

    public int getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(int recipeID) {
        this.recipeID = recipeID;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public double getRequiredQuantity() {
        return requiredQuantity;
    }

    public void setRequiredQuantity(double requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }

    public String getRequiredUnit() {
        return requiredUnit;
    }

    public void setRequiredUnit(String requiredUnit) {
        this.requiredUnit = requiredUnit;
    }

    public String getFormattedRequirement() {
        if (requiredQuantity == (long) requiredQuantity) {
            return String.format("%d %s %s", (long) requiredQuantity, requiredUnit, ingredientName);
        } else {
            return String.format("%.1f %s %s", requiredQuantity, requiredUnit, ingredientName);
        }
    }
}
