# Smart Pantry Manager (MAD700 Practical Assignment)

An Android application built in **Java** for **Mobile App Development 700 (Richfield Graduate Institute of Technology)** that helps households and students reduce food waste by tracking leftover pantry ingredients and suggesting recipes based **strictly** on what they already have at home.

---

## 1. App Overview & Core Features

- **Full Pantry CRUD Management**: Add new ingredients (`MainActivity`), view all current ingredients in a custom `RecyclerView` (`PantryListActivity`), update quantities/expiry dates, and delete items using the toolbar Delete Mode `Switch`.
- **Custom DatePickerDialog**: Uses a `DialogFragment` with a `CalendarView` widget (`DatePickerDialog.java`) to select ingredient expiry dates and highlights items expiring within 3 days.
- **Pre-Loaded Recipe Collection**: Seeds **18 practical South African and student-friendly recipes** into the database on first run (`PantryDBHelper.java`), complete with required ingredient quantities, units, preparation times, and step-by-step instructions.
- **Strict-Matching Rule Engine (`RecipeMatcher.java`)**:
  - A recipe is only listed under **Strict Matches** (`SuggestedRecipesActivity`) if **every single required ingredient** is present in the user's pantry in **at least the required quantity**.
  - Handles real-world naming and measurement variations:
    - **Singular/Plural & Synonym Normalization**: Automatically matches `"Roma Tomatoes"` or `"tomatoes"` with `"Tomato"`, `"Large Eggs"` with `"Egg"`, and `"Cooking Oil"` with `"Oil"`.
    - **Unit Conversion**: Converts compatible mass units (`mg`, `g`, `kg` -> base `g`) and volume units (`ml`, `L`, `tsp`, `tbsp`, `cup` -> base `ml`) before comparing quantities (e.g., `1 kg` of White Rice in the pantry satisfies a recipe requiring `250 g` of Rice).
  - **Bonus "Almost There" Mode**: Provides a clearly separated toggle view showing recipes missing **only 1 ingredient** so users can see what they are one item away from cooking without polluting the strict suggestions list.
- **Recipe Detail & "Cook Now" Deduction (`RecipeDetailActivity`)**: Shows each required ingredient with a live pantry check (`✓` vs `✗`) and lets the user deduct used quantities from SQLite when cooking.
- **Persistent User Preferences (`PantrySettingsActivity`)**: Uses `SharedPreferences` (`"SmartPantryPreferences"`) to persist sorting criteria (`ingredientname`, `expirydate`, `category`), sort order (`ASC` / `DESC`), expiry alert highlights, and unit system preference across app restarts.
- **Strict Scope Compliance**: Uses zero location, GPS, or Google Maps SDK services.

---

## 2. Database Option Chosen & Justification (SQLite)

This project uses **SQLite** implemented locally on-device via `SQLiteOpenHelper` (`PantryDBHelper.java`) and a dedicated data access class (`PantryDataSource.java`), consistent with Chapter 3 (*Navigation, Interface Design and Persistent Data*) and Chapter 5 (*Lists, Adapters, ViewHolders, and LayoutManagers*) of the MAD700 Learner Guide (Iversen & Eierman, 2020).

**Why SQLite was chosen over Firebase and PostgreSQL:**
1. **100% Offline Reliability**: A user checking their kitchen cupboard or pantry should never be blocked by poor cellular connectivity or Wi-Fi drops. Local SQLite guarantees instant sub-millisecond reads and writes without network latency.
2. **Relational Integrity for Recipe Matching**: Storing `ingredient`, `recipe`, and `recipe_ingredient` as structured relational tables allows fast SQL sorting (`ORDER BY ingredientname COLLATE NOCASE ASC`) and clean foreign-key relationships between recipes and their required ingredients.
3. **Zero External Server or Billing Dependencies**: Unlike cloud-hosted PostgreSQL or Firebase, on-device SQLite is built directly into the Android SDK, ensuring the marker can clone and run the project immediately in Android Studio without configuring API keys or external database credentials.

---

## 3. Setup & Run Instructions

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/<your-github-username>/SmartPantryManager.git
   ```
2. **Open in Android Studio**:
   - Launch **Android Studio** (Giraffe / Hedgehog / Iguana or later).
   - Select **File > Open** and choose the `SmartPantryManager` project folder.
   - Wait for **Gradle Sync** to complete automatically.
3. **Configure an Emulator or Physical Device**:
   - Open **Tools > Device Manager** and create/start an Android Virtual Device (e.g., **Pixel 6** running **API 30 / Android 11** or **API 34 / Android 14**), or connect a physical Android phone with **USB Debugging** enabled.
   - Ensure the project uses **JDK 17** or **JDK 11** under **File > Settings > Build, Execution, Deployment > Build Tools > Gradle**.
4. **Run the Application**:
   - Click the green **Run 'app'** button (`Shift + F10`).
   - On first launch, `PantryDBHelper` automatically seeds 18 recipes and 7 sample pantry items (`Roma Tomatoes`, `Large Eggs`, `Onions`, `Cooking Oil`, `White Bread`, `Cheddar Cheese`, `White Rice`) so you can immediately test the **Pantry List**, **Suggested Recipes (4 Strict Matches & 4 Almost There Matches)**, and **Full CRUD Operations**.
