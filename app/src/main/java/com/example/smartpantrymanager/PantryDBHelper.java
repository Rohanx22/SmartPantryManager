package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Calendar;

/**
 * SQLiteOpenHelper subclass that manages database creation, version upgrades,
 * and first-run seeding of 18 recipes and initial pantry items.
 * Modeled on ContactDBHelper (Listing 5.3) in the MAD700 Learner Guide.
 */
public class PantryDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smartpantry.db";
    private static final int DATABASE_VERSION = 1;

    // Table 1: User's Pantry Ingredients (Full CRUD)
    private static final String CREATE_TABLE_INGREDIENT =
            "create table ingredient (_id integer primary key autoincrement, "
                    + "ingredientname text not null, "
                    + "quantity real not null, "
                    + "unit text not null, "
                    + "category text, "
                    + "expirydate text);";

    // Table 2: Pre-loaded Recipes (18 seeded recipes)
    private static final String CREATE_TABLE_RECIPE =
            "create table recipe (_id integer primary key autoincrement, "
                    + "recipename text not null, "
                    + "preptime integer, "
                    + "servings integer, "
                    + "instructions text not null);";

    // Table 3: Required Ingredients for each Recipe (Foreign Key to recipe._id)
    private static final String CREATE_TABLE_RECIPE_INGREDIENT =
            "create table recipe_ingredient (_id integer primary key autoincrement, "
                    + "recipe_id integer not null, "
                    + "ingredientname text not null, "
                    + "requiredquantity real not null, "
                    + "requiredunit text not null, "
                    + "foreign key(recipe_id) references recipe(_id));";

    public PantryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_INGREDIENT);
        db.execSQL(CREATE_TABLE_RECIPE);
        db.execSQL(CREATE_TABLE_RECIPE_INGREDIENT);

        // Pre-load 18 recipes and sample pantry items on first run (Section 2.2 requirement)
        seedRecipes(db);
        seedInitialPantry(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(PantryDBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredient");
        db.execSQL("DROP TABLE IF EXISTS recipe");
        db.execSQL("DROP TABLE IF EXISTS ingredient");
        onCreate(db);
    }

    /**
     * Seeds 7 realistic leftover pantry ingredients on first run so the user can immediately
     * test Strict Matching, Almost There matching, and CRUD operations.
     */
    private void seedInitialPantry(SQLiteDatabase db) {
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.DAY_OF_YEAR, 2); // Expiring soon (within 3 days)
        insertSeedPantryItem(db, "Roma Tomatoes", 4.0, "pcs", "Vegetables", cal.getTimeInMillis());

        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 5);
        insertSeedPantryItem(db, "Large Eggs", 6.0, "pcs", "Dairy & Eggs", cal.getTimeInMillis());

        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 10);
        insertSeedPantryItem(db, "Onions", 3.0, "pcs", "Vegetables", cal.getTimeInMillis());

        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 30);
        insertSeedPantryItem(db, "Cooking Oil", 500.0, "ml", "Pantry Staples & Spices", cal.getTimeInMillis());

        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 3); // Expiring soon
        insertSeedPantryItem(db, "White Bread", 8.0, "pcs", "Grains & Pasta", cal.getTimeInMillis());

        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 14);
        insertSeedPantryItem(db, "Cheddar Cheese", 250.0, "g", "Dairy & Eggs", cal.getTimeInMillis());

        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 60);
        insertSeedPantryItem(db, "White Rice", 1.0, "kg", "Grains & Pasta", cal.getTimeInMillis());
    }

    private void insertSeedPantryItem(SQLiteDatabase db, String name, double qty, String unit, String category, long expiryMillis) {
        ContentValues cv = new ContentValues();
        cv.put("ingredientname", name);
        cv.put("quantity", qty);
        cv.put("unit", unit);
        cv.put("category", category);
        cv.put("expirydate", String.valueOf(expiryMillis));
        db.insert("ingredient", null, cv);
    }

    /**
     * Seeds 18 leftover-friendly recipes into SQLite on first run (Section 2.2 requires 15-20 recipes).
     */
    private void seedRecipes(SQLiteDatabase db) {
        // Recipe 1: Tomato & Onion Scrambled Eggs (Matches initial seeded pantry!)
        long r1 = insertRecipeHeader(db, "Tomato & Onion Scrambled Eggs", 12, 2,
                "1. Dice the onion and tomatoes finely.\n"
                + "2. Heat 15 ml (1 tbsp) of cooking oil in a frying pan over medium heat.\n"
                + "3. Fry the onion for 3 minutes until soft, then add the diced tomatoes.\n"
                + "4. Whisk the eggs in a bowl, pour into the pan, and stir gently until cooked through.");
        insertRecipeReq(db, r1, "Egg", 3, "pcs");
        insertRecipeReq(db, r1, "Tomato", 2, "pcs");
        insertRecipeReq(db, r1, "Onion", 1, "pcs");
        insertRecipeReq(db, r1, "Oil", 15, "ml");

        // Recipe 2: Classic Cheese & Tomato Toasted Braaiiekos / Sandwich (Matches initial seeded pantry!)
        long r2 = insertRecipeHeader(db, "Cheese & Tomato Toasted Sandwich", 10, 2,
                "1. Slice the tomatoes and onion into thin rings.\n"
                + "2. Grate 80 g of cheddar cheese.\n"
                + "3. Layer the cheese, tomato, and onion between 4 slices of bread.\n"
                + "4. Brush the outside lightly with oil and toast in a pan for 3 minutes per side until golden.");
        insertRecipeReq(db, r2, "Bread", 4, "pcs");
        insertRecipeReq(db, r2, "Cheese", 80, "g");
        insertRecipeReq(db, r2, "Tomato", 1, "pcs");
        insertRecipeReq(db, r2, "Onion", 1, "pcs");
        insertRecipeReq(db, r2, "Oil", 10, "ml");

        // Recipe 3: Egg Fried Rice Bowl (Matches initial seeded pantry! Tests 1 kg rice >= 250 g rice)
        long r3 = insertRecipeHeader(db, "Leftover Egg & Onion Fried Rice", 20, 2,
                "1. Rinse 250 g of rice and boil in salted water for 12-15 minutes, then drain.\n"
                + "2. Heat 30 ml oil in a wok or large pan and fry the chopped onion until golden.\n"
                + "3. Push onion to the side, crack in 2 eggs, and scramble quickly.\n"
                + "4. Toss the cooked rice and diced tomato into the pan and stir-fry on high heat for 3 minutes.");
        insertRecipeReq(db, r3, "Rice", 250, "g");
        insertRecipeReq(db, r3, "Egg", 2, "pcs");
        insertRecipeReq(db, r3, "Onion", 1, "pcs");
        insertRecipeReq(db, r3, "Tomato", 1, "pcs");
        insertRecipeReq(db, r3, "Oil", 30, "ml");

        // Recipe 4: Cheesy French Toast (Matches initial seeded pantry!)
        long r4 = insertRecipeHeader(db, "Savory Cheesy French Toast", 12, 2,
                "1. Beat 2 eggs in a shallow dish.\n"
                + "2. Dip 4 slices of bread into the beaten egg so both sides are coated.\n"
                + "3. Heat 15 ml oil in a non-stick skillet and fry bread for 2 minutes per side.\n"
                + "4. Top immediately with 60 g grated cheddar cheese and allow to melt before serving.");
        insertRecipeReq(db, r4, "Bread", 4, "pcs");
        insertRecipeReq(db, r4, "Egg", 2, "pcs");
        insertRecipeReq(db, r4, "Cheese", 60, "g");
        insertRecipeReq(db, r4, "Oil", 15, "ml");

        // Recipe 5: Chakalaka Baked Beans Skillet (Almost There with default pantry: Missing Baked Beans!)
        long r5 = insertRecipeHeader(db, "Quick Chakalaka Baked Beans", 15, 3,
                "1. Finely chop the onion and 2 tomatoes.\n"
                + "2. Heat 15 ml cooking oil in a pot and sauté the onion until translucent.\n"
                + "3. Add the chopped tomatoes and simmer for 5 minutes until a thick relish forms.\n"
                + "4. Stir in 1 tin of baked beans and heat through for 5 minutes. Serve hot on toast or pap.");
        insertRecipeReq(db, r5, "Onion", 1, "pcs");
        insertRecipeReq(db, r5, "Tomato", 2, "pcs");
        insertRecipeReq(db, r5, "Oil", 15, "ml");
        insertRecipeReq(db, r5, "Baked Beans", 1, "pcs");

        // Recipe 6: Garlic & Tomato Savoury Rice (Almost There: Missing Garlic!)
        long r6 = insertRecipeHeader(db, "Garlic & Tomato Savoury Rice", 22, 3,
                "1. Heat 20 ml oil in a saucepan and fry the chopped onion and 2 crushed garlic cloves.\n"
                + "2. Dice 2 tomatoes and stir into the pan until softened.\n"
                + "3. Add 300 g rice and 600 ml water, cover with a lid, and simmer on low for 15 minutes.");
        insertRecipeReq(db, r6, "Rice", 300, "g");
        insertRecipeReq(db, r6, "Tomato", 2, "pcs");
        insertRecipeReq(db, r6, "Onion", 1, "pcs");
        insertRecipeReq(db, r6, "Oil", 20, "ml");
        insertRecipeReq(db, r6, "Garlic", 2, "pcs");

        // Recipe 7: Spanish Potato & Onion Tortilla (Almost There: Missing Potatoes!)
        long r7 = insertRecipeHeader(db, "Spanish Potato & Onion Omelette", 25, 2,
                "1. Peel and thinly slice 3 potatoes and 1 onion.\n"
                + "2. Shallow-fry the potatoes and onion in 45 ml oil over medium-low heat until tender.\n"
                + "3. Beat 4 eggs in a bowl, stir in the warm potatoes and onion, and return to the pan.\n"
                + "4. Cook gently for 6 minutes until set, flip with a plate, and brown the underside.");
        insertRecipeReq(db, r7, "Potato", 3, "pcs");
        insertRecipeReq(db, r7, "Egg", 4, "pcs");
        insertRecipeReq(db, r7, "Onion", 1, "pcs");
        insertRecipeReq(db, r7, "Oil", 45, "ml");

        // Recipe 8: Creamy Cheese & Milk Omelette (Almost There: Missing Milk!)
        long r8 = insertRecipeHeader(db, "Fluffy Three-Egg Cheese Omelette", 10, 1,
                "1. Whisk 3 eggs with 50 ml milk until light and frothy.\n"
                + "2. Heat 10 ml oil in a frying pan over medium heat.\n"
                + "3. Pour in the egg mixture and tilt the pan as the edges set.\n"
                + "4. Sprinkle 60 g grated cheese over half the omelette, fold over, and serve.");
        insertRecipeReq(db, r8, "Egg", 3, "pcs");
        insertRecipeReq(db, r8, "Milk", 50, "ml");
        insertRecipeReq(db, r8, "Cheese", 60, "g");
        insertRecipeReq(db, r8, "Oil", 10, "ml");

        // Recipe 9: Traditional Stiff Pap & Tomatooorie (Needs Mealie Meal)
        long r9 = insertRecipeHeader(db, "Traditional Pap & Tomato Smoor", 30, 4,
                "1. Bring water to a boil in a heavy pot and slowly whisk in 400 g mealie meal.\n"
                + "2. Cover and steam on low heat for 20 minutes, stirring occasionally until stiff.\n"
                + "3. In a separate pan, fry 1 chopped onion and 3 tomatoes in 20 ml oil for 10 minutes to make the smoor.\n"
                + "4. Spoon the rich tomato and onion smoor generously over the hot pap.");
        insertRecipeReq(db, r9, "Mealie Meal", 400, "g");
        insertRecipeReq(db, r9, "Tomato", 3, "pcs");
        insertRecipeReq(db, r9, "Onion", 1, "pcs");
        insertRecipeReq(db, r9, "Oil", 20, "ml");

        // Recipe 10: Student Tuna Mayo Rice Bowl
        long r10 = insertRecipeHeader(db, "Quick Tuna & Onion Rice Bowl", 18, 2,
                "1. Boil 200 g white rice until fluffy and drain well.\n"
                + "2. Flake 1 tin of tuna and toss with finely diced onion and 1 diced tomato.\n"
                + "3. Drizzle with 10 ml oil, season, and serve over the warm rice.");
        insertRecipeReq(db, r10, "Rice", 200, "g");
        insertRecipeReq(db, r10, "Tuna", 1, "pcs");
        insertRecipeReq(db, r10, "Onion", 1, "pcs");
        insertRecipeReq(db, r10, "Tomato", 1, "pcs");
        insertRecipeReq(db, r10, "Oil", 10, "ml");

        // Recipe 11: Garlic & Herb Pasta Arrabbiata
        long r11 = insertRecipeHeader(db, "Rustic Tomato & Garlic Pasta", 20, 2,
                "1. Boil 250 g pasta in salted water for 10 minutes until al dente.\n"
                + "2. Fry 2 minced garlic cloves and 1 chopped onion in 20 ml oil.\n"
                + "3. Add 3 chopped tomatoes and simmer into a rich sauce, then toss with pasta and 50 g cheese.");
        insertRecipeReq(db, r11, "Pasta", 250, "g");
        insertRecipeReq(db, r11, "Tomato", 3, "pcs");
        insertRecipeReq(db, r11, "Garlic", 2, "pcs");
        insertRecipeReq(db, r11, "Onion", 1, "pcs");
        insertRecipeReq(db, r11, "Cheese", 50, "g");
        insertRecipeReq(db, r11, "Oil", 20, "ml");

        // Recipe 12: Savoury Beef Mince & Rice Skillet
        long r12 = insertRecipeHeader(db, "One-Pan Savoury Mince & Rice", 25, 3,
                "1. Brown 300 g beef mince with 1 chopped onion in 15 ml oil.\n"
                + "2. Stir in 2 diced tomatoes and 200 g uncooked rice with 450 ml water.\n"
                + "3. Cover and simmer for 18 minutes until the rice absorbs all the savoury juices.");
        insertRecipeReq(db, r12, "Mince", 300, "g");
        insertRecipeReq(db, r12, "Rice", 200, "g");
        insertRecipeReq(db, r12, "Onion", 1, "pcs");
        insertRecipeReq(db, r12, "Tomato", 2, "pcs");
        insertRecipeReq(db, r12, "Oil", 15, "ml");

        // Recipe 13: Chicken & Vegetable Stir-Fry with Rice
        long r13 = insertRecipeHeader(db, "Pan-Seared Chicken & Tomato Rice", 25, 2,
                "1. Cut 250 g chicken breast into strips and sear in 20 ml hot oil.\n"
                + "2. Add 1 sliced onion and 2 chopped tomatoes and cook for 6 minutes.\n"
                + "3. Serve hot over 200 g steamed rice.");
        insertRecipeReq(db, r13, "Chicken", 250, "g");
        insertRecipeReq(db, r13, "Rice", 200, "g");
        insertRecipeReq(db, r13, "Onion", 1, "pcs");
        insertRecipeReq(db, r13, "Tomato", 2, "pcs");
        insertRecipeReq(db, r13, "Oil", 20, "ml");

        // Recipe 14: Creamy Oats Breakfast Porridge
        long r14 = insertRecipeHeader(db, "Warm Creamy Milk & Banana Oats", 10, 1,
                "1. Combine 80 g oats with 200 ml milk in a small saucepan.\n"
                + "2. Simmer over low heat for 5 minutes while stirring continuously.\n"
                + "3. Slice 1 ripe banana on top and serve warm.");
        insertRecipeReq(db, r14, "Oats", 80, "g");
        insertRecipeReq(db, r14, "Milk", 200, "ml");
        insertRecipeReq(db, r14, "Banana", 1, "pcs");

        // Recipe 15:Three-Ingredient Flapjacks / Pancakes
        long r15 = insertRecipeHeader(db, "Pantry Staple Flapjacks", 15, 2,
                "1. Whisk 150 g flour, 200 ml milk, and 2 eggs into a smooth batter.\n"
                + "2. Heat 10 ml oil in a flat pan over medium heat.\n"
                + "3. Spoon batter into rounds and flip when bubbles appear on the surface.");
        insertRecipeReq(db, r15, "Flour", 150, "g");
        insertRecipeReq(db, r15, "Milk", 200, "ml");
        insertRecipeReq(db, r15, "Egg", 2, "pcs");
        insertRecipeReq(db, r15, "Oil", 10, "ml");

        // Recipe 16: Loaded Baked Potato with Cheese
        long r16 = insertRecipeHeader(db, "Crispy Jacket Potato with Melted Cheddar", 35, 1,
                "1. Prick 2 large potatoes with a fork, rub with 10 ml oil, and bake or microwave until tender.\n"
                + "2. Slice open the hot potatoes and pile 80 g grated cheddar cheese inside until melted.");
        insertRecipeReq(db, r16, "Potato", 2, "pcs");
        insertRecipeReq(db, r16, "Cheese", 80, "g");
        insertRecipeReq(db, r16, "Oil", 10, "ml");

        // Recipe 17: Spinach & Feta/Cheese Egg Muffins
        long r17 = insertRecipeHeader(db, "Spinach & Cheddar Baked Egg Cups", 20, 2,
                "1. Wilt 100 g chopped spinach in a pan with 10 ml oil.\n"
                + "2. Whisk 4 eggs with 60 g grated cheese and stir in the spinach.\n"
                + "3. Bake in greased muffin cups at 180°C for 15 minutes until puffed and golden.");
        insertRecipeReq(db, r17, "Spinach", 100, "g");
        insertRecipeReq(db, r17, "Egg", 4, "pcs");
        insertRecipeReq(db, r17, "Cheese", 60, "g");
        insertRecipeReq(db, r17, "Oil", 10, "ml");

        // Recipe 18: Carrot & Potato Country Soup
        long r18 = insertRecipeHeader(db, "Hearty Carrot, Potato & Onion Soup", 30, 3,
                "1. Chop 3 carrots, 2 potatoes, and 1 onion into cubes.\n"
                + "2. Sweat the vegetables in 20 ml oil for 5 minutes.\n"
                + "3. Add water to cover, simmer for 20 minutes until soft, and mash until thick and smooth.");
        insertRecipeReq(db, r18, "Carrot", 3, "pcs");
        insertRecipeReq(db, r18, "Potato", 2, "pcs");
        insertRecipeReq(db, r18, "Onion", 1, "pcs");
        insertRecipeReq(db, r18, "Oil", 20, "ml");
    }

    private long insertRecipeHeader(SQLiteDatabase db, String name, int prepTime, int servings, String instructions) {
        ContentValues cv = new ContentValues();
        cv.put("recipename", name);
        cv.put("preptime", prepTime);
        cv.put("servings", servings);
        cv.put("instructions", instructions);
        return db.insert("recipe", null, cv);
    }

    private void insertRecipeReq(SQLiteDatabase db, long recipeId, String ingName, double qty, String unit) {
        ContentValues cv = new ContentValues();
        cv.put("recipe_id", recipeId);
        cv.put("ingredientname", ingName);
        cv.put("requiredquantity", qty);
        cv.put("requiredunit", unit);
        db.insert("recipe_ingredient", null, cv);
    }
}
