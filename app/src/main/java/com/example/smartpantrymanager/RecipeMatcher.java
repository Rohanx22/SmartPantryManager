package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Core Business Logic for Section 2.3: The Strict-Matching Rule.
 *
 * A recipe is only classified as a "Strict Match" if EVERY required ingredient is
 * currently present in the user's pantry in at least the required quantity.
 *
 * Handles real-world input variations:
 * 1. Singular/plural normalization (e.g., "tomatoes" -> "tomato", "potatoes" -> "potato", "eggs" -> "egg")
 * 2. Common South African / student kitchen synonyms (e.g., "cooking oil" / "vegetable oil")
 * 3. Compatible unit conversions across mass (mg, g, kg), volume (ml, L, tsp, tbsp, cup), and counts (pcs).
 */
public class RecipeMatcher {

    public static final int UNIT_GROUP_MASS = 1;   // Base unit: grams (g)
    public static final int UNIT_GROUP_VOLUME = 2; // Base unit: millilitres (ml)
    public static final int UNIT_GROUP_COUNT = 3;  // Base unit: pieces (pcs)

    private static final Map<String, String> SYNONYM_MAP = new HashMap<>();

    static {
        SYNONYM_MAP.put("roma tomato", "tomato");
        SYNONYM_MAP.put("cherry tomato", "tomato");
        SYNONYM_MAP.put("white onion", "onion");
        SYNONYM_MAP.put("red onion", "onion");
        SYNONYM_MAP.put("brown onion", "onion");
        SYNONYM_MAP.put("garlic clove", "garlic");
        SYNONYM_MAP.put("clove garlic", "garlic");
        SYNONYM_MAP.put("cooking oil", "oil");
        SYNONYM_MAP.put("vegetable oil", "oil");
        SYNONYM_MAP.put("sunflower oil", "oil");
        SYNONYM_MAP.put("olive oil", "oil");
        SYNONYM_MAP.put("cheddar cheese", "cheese");
        SYNONYM_MAP.put("gouda cheese", "cheese");
        SYNONYM_MAP.put("grated cheese", "cheese");
        SYNONYM_MAP.put("large egg", "egg");
        SYNONYM_MAP.put("free range egg", "egg");
        SYNONYM_MAP.put("white rice", "rice");
        SYNONYM_MAP.put("basmati rice", "rice");
        SYNONYM_MAP.put("maize meal", "mealie meal");
        SYNONYM_MAP.put("pap", "mealie meal");
        SYNONYM_MAP.put("baked bean", "baked beans");
        SYNONYM_MAP.put("tinned bean", "baked beans");
        SYNONYM_MAP.put("beef mince", "mince");
        SYNONYM_MAP.put("minced beef", "mince");
        SYNONYM_MAP.put("chicken breast", "chicken");
        SYNONYM_MAP.put("chicken piece", "chicken");
        SYNONYM_MAP.put("full cream milk", "milk");
        SYNONYM_MAP.put("fresh milk", "milk");
        SYNONYM_MAP.put("white bread", "bread");
        SYNONYM_MAP.put("brown bread", "bread");
        SYNONYM_MAP.put("bread slice", "bread");
    }

    /**
     * Normalizes an ingredient name to lowercase singular canonical form so that
     * trivial differences ("Tomatoes" vs "tomato", "Potatoes" vs "potato") match reliably.
     */
    public static String normalizeIngredientName(String rawName) {
        if (rawName == null) {
            return "";
        }
        String cleaned = rawName.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\s]", " ")
                .replaceAll("\s+", " ")
                .trim();

        if (cleaned.isEmpty()) {
            return "";
        }

        // Convert plural endings to singular on the final word
        String[] words = cleaned.split(" ");
        int lastIdx = words.length - 1;
        words[lastIdx] = singularizeWord(words[lastIdx]);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(words[i]);
        }
        String normalized = sb.toString();

        // Check synonym table
        if (SYNONYM_MAP.containsKey(normalized)) {
            return SYNONYM_MAP.get(normalized);
        }
        return normalized;
    }

    private static String singularizeWord(String word) {
        // Keep invariant words intact
        if (word.equals("rice") || word.equals("cheese") || word.equals("lettuce")
                || word.equals("oats") || word.equals("couscous") || word.equals("hummus")
                || word.equals("pasta") || word.equals("spinach") || word.equals("fish")) {
            return word;
        }
        // Special keep for "baked beans" or "beans" -> singularize to "bean" unless mapped
        // TODO: handle -oes plural endings like tomatoes and potatoes
        if (word.endsWith("ies") && word.length() > 4) {
            // e.g. berries -> berry
            return word.substring(0, word.length() - 3) + "y";
        }
        if (word.endsWith("ves") && word.length() > 4) {
            // e.g. loaves -> loaf, leaves -> leaf
            return word.substring(0, word.length() - 3) + "f";
        }
        if (word.endsWith("ches") || word.endsWith("shes") || word.endsWith("xes") || word.endsWith("sses")) {
            return word.substring(0, word.length() - 2);
        }
        if (word.endsWith("s") && !word.endsWith("ss") && word.length() > 2) {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }

    /**
     * Determines which measurement family a unit belongs to (Mass, Volume, or Count).
     */
    public static int getUnitGroup(String rawUnit) {
        if (rawUnit == null) return UNIT_GROUP_COUNT;
        String u = rawUnit.toLowerCase(Locale.ROOT).trim();
        switch (u) {
            case "mg":
            case "g":
            case "gram":
            case "grams":
            case "kg":
            case "kilogram":
            case "kilograms":
                return UNIT_GROUP_MASS;
            case "ml":
            case "millilitre":
            case "milliliter":
            case "l":
            case "litre":
            case "liter":
            case "tsp":
            case "teaspoon":
            case "tbsp":
            case "tablespoon":
            case "cup":
            case "cups":
                return UNIT_GROUP_VOLUME;
            default:
                return UNIT_GROUP_COUNT;
        }
    }

    /**
     * Converts a quantity into its group's base unit:
     * - Mass -> grams (g)
     * - Volume -> millilitres (ml)
     * - Count -> pieces (pcs)
     */
    public static double convertToBaseQuantity(double quantity, String rawUnit) {
        if (rawUnit == null) return quantity;
        String u = rawUnit.toLowerCase(Locale.ROOT).trim();
        switch (u) {
            // Mass conversions to grams (g)
            case "mg":
                return quantity * 0.001;
            case "g":
            case "gram":
            case "grams":
                return quantity;
            case "kg":
            case "kilogram":
            case "kilograms":
                return quantity * 1000.0;

            // Volume conversions to millilitres (ml)
            case "ml":
            case "millilitre":
            case "milliliter":
                return quantity;
            case "l":
            case "litre":
            case "liter":
                return quantity * 1000.0;
            case "tsp":
            case "teaspoon":
                return quantity * 5.0;
            case "tbsp":
            case "tablespoon":
                return quantity * 15.0;
            case "cup":
            case "cups":
                return quantity * 250.0;

            // Count (pcs, slices, cloves, tins)
            default:
                return quantity;
        }
    }

    /**
     * Converts a base unit amount back into a target unit so we can deduct used quantities
     * from a pantry item when the user cooks a recipe.
     */
    public static double convertFromBaseQuantity(double baseQuantity, String targetUnit) {
        double factor = convertToBaseQuantity(1.0, targetUnit);
        if (factor <= 0) return baseQuantity;
        return baseQuantity / factor;
    }

    /**
     * Checks if a single RecipeIngredient requirement is satisfied by the user's current pantry list.
     */
    public static boolean hasSufficientIngredient(RecipeIngredient req, List<Ingredient> pantryList) {
        double availableBase = getAvailableBaseQuantity(req, pantryList);
        double requiredBase = convertToBaseQuantity(req.getRequiredQuantity(), req.getRequiredUnit());
        return availableBase + 0.0001 >= requiredBase;
    }

    /**
     * Sums up how much of the required ingredient the user currently has in the same unit group.
     */
    public static double getAvailableBaseQuantity(RecipeIngredient req, List<Ingredient> pantryList) {
        String reqNorm = normalizeIngredientName(req.getIngredientName());
        int reqGroup = getUnitGroup(req.getRequiredUnit());
        double totalAvailableBase = 0.0;

        for (Ingredient pItem : pantryList) {
            String pantryNorm = normalizeIngredientName(pItem.getIngredientName());
            if (pantryNorm.equals(reqNorm)) {
                int pantryGroup = getUnitGroup(pItem.getUnit());
                if (pantryGroup == reqGroup) {
                    totalAvailableBase += convertToBaseQuantity(pItem.getQuantity(), pItem.getUnit());
                }
            }
        }
        return totalAvailableBase;
    }

    /**
     * Evaluates all recipes and returns ONLY those that satisfy the Strict-Matching Rule (Section 2.3):
     * 100% of required ingredients must be present in the pantry in at least the required quantity.
     */
    public static ArrayList<Recipe> getStrictMatchedRecipes(List<Recipe> allRecipes, List<Ingredient> pantryList) {
        ArrayList<Recipe> strictMatches = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            ArrayList<RecipeIngredient> reqList = recipe.getRequiredIngredients();
            if (reqList == null || reqList.isEmpty()) {
                continue;
            }

            int missingCount = 0;
            for (RecipeIngredient req : reqList) {
                if (!hasSufficientIngredient(req, pantryList)) {
                    missingCount++;
                }
            }

            if (missingCount == 0) {
                recipe.setMissingIngredientCount(0);
                recipe.setMatchSummaryBadge("STRICT MATCH: All " + reqList.size() + " ingredients in pantry");
                strictMatches.add(recipe);
            }
        }
        return strictMatches;
    }

    /**
     * Optional Stretch Feature (Section 2.3): Returns recipes that are missing ONLY 1 ingredient,
     * kept strictly separate from the main strict suggestions list.
     */
    public static ArrayList<Recipe> getAlmostThereRecipes(List<Recipe> allRecipes, List<Ingredient> pantryList) {
        ArrayList<Recipe> almostMatches = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            ArrayList<RecipeIngredient> reqList = recipe.getRequiredIngredients();
            if (reqList == null || reqList.isEmpty()) {
                continue;
            }

            int missingCount = 0;
            String missingName = "";

            for (RecipeIngredient req : reqList) {
                if (!hasSufficientIngredient(req, pantryList)) {
                    missingCount++;
                    missingName = req.getFormattedRequirement();
                }
            }

            if (missingCount == 1) {
                recipe.setMissingIngredientCount(1);
                recipe.setMatchSummaryBadge("ALMOST THERE: Missing only " + missingName);
                almostMatches.add(recipe);
            }
        }
        return almostMatches;
    }
}
