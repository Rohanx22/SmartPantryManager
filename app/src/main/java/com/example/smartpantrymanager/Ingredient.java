package com.example.smartpantrymanager;

import java.util.Calendar;

/**
 * Model class representing a single ingredient stored in the user's pantry.
 * Modeled on the POJO structure from Chapter 3 (Listing 5.5) of the MAD700 Learner Guide.
 */
public class Ingredient {

    private int ingredientID;
    private String ingredientName;
    private double quantity;
    private String unit;
    private String category;
    private Calendar expiryDate;

    public Ingredient() {
        // Default ID of -1 indicates a new record that has not been inserted into SQLite yet
        ingredientID = -1;
        ingredientName = "";
        quantity = 0.0;
        unit = "pcs";
        category = "Vegetables";
        expiryDate = Calendar.getInstance();
        // Default expiry to 7 days from today
        expiryDate.add(Calendar.DAY_OF_YEAR, 7);
    }

    public int getIngredientID() {
        return ingredientID;
    }

    public void setIngredientID(int i) {
        ingredientID = i;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String s) {
        ingredientName = s;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double q) {
        quantity = q;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String u) {
        unit = u;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String c) {
        category = c;
    }

    public Calendar getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Calendar c) {
        expiryDate = c;
    }

    /**
     * Helper method to format quantity cleanly without trailing .0 for whole numbers.
     */
    public String getFormattedQuantity() {
        if (quantity == (long) quantity) {
            return String.format("%d %s", (long) quantity, unit);
        } else {
            return String.format("%.1f %s", quantity, unit);
        }
    }
}
