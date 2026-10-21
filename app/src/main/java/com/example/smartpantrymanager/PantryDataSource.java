package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Data source class providing open/close database management and full CRUD queries
 * for Pantry Ingredients as well as queries for pre-loaded Recipes.
 * Modeled directly on ContactDataSource (Chapters 3 & 5) in the MAD700 Learner Guide.
 */
public class PantryDataSource {

    private SQLiteDatabase database;
    private PantryDBHelper dbHelper;

    public PantryDataSource(Context context) {
        dbHelper = new PantryDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * CREATE: Inserts a new Ingredient record into the ingredient table (Listing 5.6 pattern).
     */
    public boolean insertIngredient(Ingredient i) {
        boolean didSucceed = false;
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put("ingredientname", i.getIngredientName());
            initialValues.put("quantity", i.getQuantity());
            initialValues.put("unit", i.getUnit());
            initialValues.put("category", i.getCategory());
            initialValues.put("expirydate", String.valueOf(i.getExpiryDate().getTimeInMillis()));

            didSucceed = database.insert("ingredient", null, initialValues) > 0;
        } catch (Exception e) {
            // Do nothing - will return false if there is an exception
        }
        return didSucceed;
    }

    /**
     * UPDATE: Updates an existing Ingredient record in the ingredient table by _id (Listing 5.6 pattern).
     */
    public boolean updateIngredient(Ingredient i) {
        boolean didSucceed = false;
        try {
            Long rowId = (long) i.getIngredientID();
            ContentValues updateValues = new ContentValues();
            updateValues.put("ingredientname", i.getIngredientName());
            updateValues.put("quantity", i.getQuantity());
            updateValues.put("unit", i.getUnit());
            updateValues.put("category", i.getCategory());
            updateValues.put("expirydate", String.valueOf(i.getExpiryDate().getTimeInMillis()));

            didSucceed = database.update("ingredient", updateValues, "_id=" + rowId, null) > 0;
        } catch (Exception e) {
            // Do nothing - will return false if there is an exception
        }
        return didSucceed;
    }

    /**
     * Retrieves the newly auto-incremented ID after inserting an ingredient (Listing 5.10 pattern).
     */
    public int getLastIngredientId() {
        int lastId;
        try {
            String query = "Select MAX(_id) from ingredient";
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            lastId = cursor.getInt(0);
            cursor.close();
        } catch (Exception e) {
            lastId = -1;
        }
        return lastId;
    }

    /**
     * READ (All): Retrieves all pantry ingredients sorted according to user SharedPreferences (Listing 6.6 pattern).
     */
    public ArrayList<Ingredient> getIngredients(String sortField, String sortOrder) {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        try {
            // Validate sort columns to avoid SQL errors
            if (!sortField.equals("ingredientname") && !sortField.equals("expirydate") && !sortField.equals("category")) {
                sortField = "ingredientname";
            }
            if (!sortOrder.equalsIgnoreCase("ASC") && !sortOrder.equalsIgnoreCase("DESC")) {
                sortOrder = "ASC";
            }

            String query = "SELECT * FROM ingredient ORDER BY " + sortField + " COLLATE NOCASE " + sortOrder;
            Cursor cursor = database.rawQuery(query, null);

            Ingredient newItem;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                newItem = new Ingredient();
                newItem.setIngredientID(cursor.getInt(0));
                newItem.setIngredientName(cursor.getString(1));
                newItem.setQuantity(cursor.getDouble(2));
                newItem.setUnit(cursor.getString(3));
                newItem.setCategory(cursor.getString(4));

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.valueOf(cursor.getString(5)));
                newItem.setExpiryDate(calendar);

                ingredients.add(newItem);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            ingredients = new ArrayList<>();
        }
        return ingredients;
    }

    /**
     * READ (Single): Retrieves a specific pantry ingredient by its primary key _id (Listing 6.9 pattern).
     */
    public Ingredient getSpecificIngredient(int ingredientId) {
        Ingredient item = new Ingredient();
        String query = "SELECT * FROM ingredient WHERE _id =" + ingredientId;
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            item.setIngredientID(cursor.getInt(0));
            item.setIngredientName(cursor.getString(1));
            item.setQuantity(cursor.getDouble(2));
            item.setUnit(cursor.getString(3));
            item.setCategory(cursor.getString(4));

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(cursor.getString(5)));
            item.setExpiryDate(calendar);

            cursor.close();
        }
        return item;
    }

    /**
     * DELETE: Deletes a pantry ingredient from SQLite by its _id (Listing 6.13 pattern).
     */
    public boolean deleteIngredient(int ingredientId) {
        boolean didDelete = false;
        try {
            didDelete = database.delete("ingredient", "_id=" + ingredientId, null) > 0;
        } catch (Exception e) {
            // Do nothing - return value already set to false
        }
        return didDelete;
    }

    /**
     * Retrieves all pre-loaded recipes along with their required ingredients from SQLite.
     */
    public ArrayList<Recipe> getAllRecipes() {
        ArrayList<Recipe> recipes = new ArrayList<>();
        try {
            String query = "SELECT _id, recipename, preptime, servings, instructions FROM recipe ORDER BY _id ASC";
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Recipe r = new Recipe();
                int rId = cursor.getInt(0);
                r.setRecipeID(rId);
                r.setRecipeName(cursor.getString(1));
                r.setPrepTimeMinutes(cursor.getInt(2));
                r.setServings(cursor.getInt(3));
                r.setInstructions(cursor.getString(4));
                r.setRequiredIngredients(getRecipeIngredients(rId));

                recipes.add(r);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            recipes = new ArrayList<>();
        }
        return recipes;
    }

    /**
     * Retrieves a single Recipe and its required ingredients by recipe _id.
     */
    public Recipe getSpecificRecipe(int recipeId) {
        Recipe r = new Recipe();
        try {
            String query = "SELECT _id, recipename, preptime, servings, instructions FROM recipe WHERE _id=" + recipeId;
            Cursor cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                r.setRecipeID(cursor.getInt(0));
                r.setRecipeName(cursor.getString(1));
                r.setPrepTimeMinutes(cursor.getInt(2));
                r.setServings(cursor.getInt(3));
                r.setInstructions(cursor.getString(4));
                r.setRequiredIngredients(getRecipeIngredients(recipeId));
            }
            cursor.close();
        } catch (Exception e) {
            // Return default recipe object
        }
        return r;
    }

    /**
     * Helper method to load all required ingredients for a specific recipe_id.
     */
    public ArrayList<RecipeIngredient> getRecipeIngredients(int recipeId) {
        ArrayList<RecipeIngredient> reqList = new ArrayList<>();
        try {
            String query = "SELECT _id, recipe_id, ingredientname, requiredquantity, requiredunit "
                    + "FROM recipe_ingredient WHERE recipe_id=" + recipeId;
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                RecipeIngredient ri = new RecipeIngredient();
                ri.setRecipeIngredientID(cursor.getInt(0));
                ri.setRecipeID(cursor.getInt(1));
                ri.setIngredientName(cursor.getString(2));
                ri.setRequiredQuantity(cursor.getDouble(3));
                ri.setRequiredUnit(cursor.getString(4));
                reqList.add(ri);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            reqList = new ArrayList<>();
        }
        return reqList;
    }

    /**
     * Deducts the ingredients used when cooking a recipe from the user's SQLite pantry.
     * If an ingredient's remaining quantity reaches 0, it is removed from the pantry.
     */
    public boolean deductRecipeFromPantry(Recipe recipe) {
        boolean allUpdated = true;
        try {
            ArrayList<Ingredient> currentPantry = getIngredients("ingredientname", "ASC");
            for (RecipeIngredient req : recipe.getRequiredIngredients()) {
                String reqNorm = RecipeMatcher.normalizeIngredientName(req.getIngredientName());
                int reqGroup = RecipeMatcher.getUnitGroup(req.getRequiredUnit());
                double remainingBaseToDeduct = RecipeMatcher.convertToBaseQuantity(
                        req.getRequiredQuantity(), req.getRequiredUnit());

                for (Ingredient pItem : currentPantry) {
                    if (remainingBaseToDeduct <= 0.0001) break;
                    String pantryNorm = RecipeMatcher.normalizeIngredientName(pItem.getIngredientName());
                    int pantryGroup = RecipeMatcher.getUnitGroup(pItem.getUnit());

                    if (pantryNorm.equals(reqNorm) && pantryGroup == reqGroup) {
                        double itemBase = RecipeMatcher.convertToBaseQuantity(pItem.getQuantity(), pItem.getUnit());
                        if (itemBase <= remainingBaseToDeduct + 0.0001) {
                            remainingBaseToDeduct -= itemBase;
                            deleteIngredient(pItem.getIngredientID());
                        } else {
                            double newBase = itemBase - remainingBaseToDeduct;
                            double newQtyInItemUnit = RecipeMatcher.convertFromBaseQuantity(newBase, pItem.getUnit());
                            pItem.setQuantity(Math.round(newQtyInItemUnit * 100.0) / 100.0);
                            updateIngredient(pItem);
                            remainingBaseToDeduct = 0;
                        }
                    }
                }
            }
        } catch (Exception e) {
            allUpdated = false;
        }
        return allUpdated;
    }
}
