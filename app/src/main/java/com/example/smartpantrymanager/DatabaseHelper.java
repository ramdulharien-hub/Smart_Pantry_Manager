package com.example.smartpantrymanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Pantry items table
        db.execSQL(
                "CREATE TABLE pantry_items (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL, " +
                        "expiry_date TEXT)"
        );

        // Recipes table
        db.execSQL(
                "CREATE TABLE recipes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "instructions TEXT NOT NULL)"
        );

        // Recipe ingredients table
        db.execSQL(
                "CREATE TABLE recipe_ingredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_id INTEGER NOT NULL, " +
                        "ingredient_name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL, " +
                        "FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)"
        );

        seedRecipes(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS pantry_items");

        onCreate(db);
    }
    // CREATE - Add a pantry item
    public long addPantryItem(PantryItem item) {

        SQLiteDatabase db = this.getWritableDatabase();

        android.content.ContentValues values = new android.content.ContentValues();

        values.put("name", item.getName());
        values.put("quantity", item.getQuantity());
        values.put("unit", item.getUnit());
        values.put("expiry_date", item.getExpiryDate());

        long id = db.insert("pantry_items", null, values);

        db.close();

        return id;
    }


    // READ - Get all pantry items
    public java.util.ArrayList<PantryItem> getAllPantryItems() {

        java.util.ArrayList<PantryItem> pantryItems =
                new java.util.ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        android.database.Cursor cursor = db.rawQuery(
                "SELECT * FROM pantry_items ORDER BY name ASC",
                null
        );

        if (cursor.moveToFirst()) {

            do {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name")
                );

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow("quantity")
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow("unit")
                );

                String expiryDate = cursor.getString(
                        cursor.getColumnIndexOrThrow("expiry_date")
                );

                PantryItem item = new PantryItem(
                        id,
                        name,
                        quantity,
                        unit,
                        expiryDate
                );

                pantryItems.add(item);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return pantryItems;
    }


    // UPDATE - Edit a pantry item
    public int updatePantryItem(PantryItem item) {

        SQLiteDatabase db = this.getWritableDatabase();

        android.content.ContentValues values = new android.content.ContentValues();

        values.put("name", item.getName());
        values.put("quantity", item.getQuantity());
        values.put("unit", item.getUnit());
        values.put("expiry_date", item.getExpiryDate());

        int rowsUpdated = db.update(
                "pantry_items",
                values,
                "id = ?",
                new String[]{String.valueOf(item.getId())}
        );

        db.close();

        return rowsUpdated;
    }


    // DELETE - Delete a pantry item
    public int deletePantryItem(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        int rowsDeleted = db.delete(
                "pantry_items",
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();

        return rowsDeleted;
    }
    // ADD RECIPE
    public long addRecipe(Recipe recipe) {

        SQLiteDatabase db = this.getWritableDatabase();

        android.content.ContentValues values =
                new android.content.ContentValues();

        values.put("name", recipe.getName());
        values.put("instructions", recipe.getInstructions());

        long recipeId = db.insert(
                "recipes",
                null,
                values
        );

        db.close();

        return recipeId;
    }


    // ADD RECIPE INGREDIENT
    public long addRecipeIngredient(
            RecipeIngredient ingredient) {

        SQLiteDatabase db = this.getWritableDatabase();

        android.content.ContentValues values =
                new android.content.ContentValues();

        values.put(
                "recipe_id",
                ingredient.getRecipeId()
        );

        values.put(
                "ingredient_name",
                ingredient.getIngredientName()
        );

        values.put(
                "quantity",
                ingredient.getQuantity()
        );

        values.put(
                "unit",
                ingredient.getUnit()
        );

        long result = db.insert(
                "recipe_ingredients",
                null,
                values
        );

        db.close();

        return result;
    }


    // GET ALL RECIPES
    public java.util.ArrayList<Recipe> getAllRecipes() {

        java.util.ArrayList<Recipe> recipes =
                new java.util.ArrayList<>();

        SQLiteDatabase db =
                this.getReadableDatabase();

        android.database.Cursor cursor =
                db.rawQuery(
                        "SELECT * FROM recipes ORDER BY name ASC",
                        null
                );

        if (cursor.moveToFirst()) {

            do {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name")
                );

                String instructions = cursor.getString(
                        cursor.getColumnIndexOrThrow("instructions")
                );

                recipes.add(
                        new Recipe(
                                id,
                                name,
                                instructions
                        )
                );

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return recipes;
    }


    // GET INGREDIENTS FOR A RECIPE
    public java.util.ArrayList<RecipeIngredient>
    getRecipeIngredients(int recipeId) {

        java.util.ArrayList<RecipeIngredient> ingredients =
                new java.util.ArrayList<>();

        SQLiteDatabase db =
                this.getReadableDatabase();

        android.database.Cursor cursor =
                db.rawQuery(
                        "SELECT * FROM recipe_ingredients " +
                                "WHERE recipe_id = ?",
                        new String[]{
                                String.valueOf(recipeId)
                        }
                );

        if (cursor.moveToFirst()) {

            do {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "ingredient_name"
                        )
                );

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                "quantity"
                        )
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow("unit")
                );

                ingredients.add(
                        new RecipeIngredient(
                                id,
                                recipeId,
                                name,
                                quantity,
                                unit
                        )
                );

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return ingredients;
    }

    public void deleteAllPantryItems() {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(
                "pantry_items",
                null,
                null
        );

        db.close();
    }

    private void seedRecipes(SQLiteDatabase db) {

        // Recipe 1
        addSeedRecipe(
                db,
                "Chicken Fried Rice",
                "Cook the rice. Stir-fry the chicken and onion. " +
                        "Add the egg and cooked rice. Mix well and serve.",
                new String[]{"Chicken", "Rice", "Egg", "Onion"},
                new double[]{200, 300, 2, 1},
                new String[]{"g", "g", "pieces", "pieces"}
        );

        // Recipe 2
        addSeedRecipe(
                db,
                "Cheese Omelette",
                "Beat the eggs. Cook them in a pan and add cheese. " +
                        "Fold the omelette and serve.",
                new String[]{"Egg", "Cheese"},
                new double[]{3, 50},
                new String[]{"pieces", "g"}
        );

        // Recipe 3
        addSeedRecipe(
                db,
                "Tuna Sandwich",
                "Mix tuna with a little mayonnaise. Place the mixture " +
                        "between slices of bread and serve.",
                new String[]{"Tuna", "Bread", "Mayonnaise"},
                new double[]{100, 2, 30},
                new String[]{"g", "slices", "g"}
        );

        // Recipe 4
        addSeedRecipe(
                db,
                "Chicken Sandwich",
                "Cook the chicken. Place chicken, tomato and cheese " +
                        "between slices of bread.",
                new String[]{"Chicken", "Bread", "Tomato", "Cheese"},
                new double[]{150, 2, 50, 30},
                new String[]{"g", "slices", "g", "g"}
        );

        // Recipe 5
        addSeedRecipe(
                db,
                "Tomato Pasta",
                "Cook the pasta. Fry tomato, onion and garlic. " +
                        "Mix with pasta and serve.",
                new String[]{"Pasta", "Tomato", "Onion", "Garlic"},
                new double[]{200, 150, 1, 10},
                new String[]{"g", "g", "pieces", "g"}
        );

        // Recipe 6
        addSeedRecipe(
                db,
                "Chicken Pasta",
                "Cook pasta and chicken. Combine with tomato sauce " +
                        "and cheese.",
                new String[]{"Pasta", "Chicken", "Tomato", "Cheese"},
                new double[]{200, 200, 150, 40},
                new String[]{"g", "g", "g", "g"}
        );

        // Recipe 7
        addSeedRecipe(
                db,
                "Mashed Potatoes",
                "Boil the potatoes until soft. Mash with butter and milk.",
                new String[]{"Potato", "Butter", "Milk"},
                new double[]{500, 30, 100},
                new String[]{"g", "g", "ml"}
        );

        // Recipe 8
        addSeedRecipe(
                db,
                "Egg Toast",
                "Fry the egg and serve it on toasted bread with butter.",
                new String[]{"Egg", "Bread", "Butter"},
                new double[]{2, 2, 10},
                new String[]{"pieces", "slices", "g"}
        );

        // Recipe 9
        addSeedRecipe(
                db,
                "Chicken and Potato",
                "Cook the chicken and potatoes with onion and garlic " +
                        "until fully cooked.",
                new String[]{"Chicken", "Potato", "Onion", "Garlic"},
                new double[]{250, 300, 1, 10},
                new String[]{"g", "g", "pieces", "g"}
        );

        // Recipe 10
        addSeedRecipe(
                db,
                "Cheese Pasta",
                "Cook pasta. Add cheese and milk and stir until creamy.",
                new String[]{"Pasta", "Cheese", "Milk"},
                new double[]{200, 80, 100},
                new String[]{"g", "g", "ml"}
        );

        // Recipe 11
        addSeedRecipe(
                db,
                "Vegetable Rice",
                "Cook rice. Fry onion and carrot, then mix with the rice.",
                new String[]{"Rice", "Carrot", "Onion"},
                new double[]{300, 100, 1},
                new String[]{"g", "g", "pieces"}
        );

        // Recipe 12
        addSeedRecipe(
                db,
                "Chicken Rice Bowl",
                "Cook the chicken and rice. Combine and serve with tomato.",
                new String[]{"Chicken", "Rice", "Tomato"},
                new double[]{200, 300, 100},
                new String[]{"g", "g", "g"}
        );

        // Recipe 13
        addSeedRecipe(
                db,
                "French Toast",
                "Dip bread in beaten egg and milk. Fry until golden.",
                new String[]{"Bread", "Egg", "Milk", "Butter"},
                new double[]{3, 2, 100, 15},
                new String[]{"slices", "pieces", "ml", "g"}
        );

        // Recipe 14
        addSeedRecipe(
                db,
                "Tuna Pasta",
                "Cook pasta. Mix with tuna, tomato and onion.",
                new String[]{"Pasta", "Tuna", "Tomato", "Onion"},
                new double[]{200, 100, 100, 1},
                new String[]{"g", "g", "g", "pieces"}
        );

        // Recipe 15
        addSeedRecipe(
                db,
                "Potato Omelette",
                "Cook sliced potatoes. Add beaten eggs and cook until set.",
                new String[]{"Potato", "Egg", "Onion"},
                new double[]{300, 3, 1},
                new String[]{"g", "pieces", "pieces"}
        );

        // Recipe 16
        addSeedRecipe(
                db,
                "Chicken Cheese Toast",
                "Cook the chicken. Place chicken and cheese on bread " +
                        "and toast until the cheese melts.",
                new String[]{"Chicken", "Bread", "Cheese"},
                new double[]{150, 2, 50},
                new String[]{"g", "slices", "g"}
        );

        // Recipe 17
        addSeedRecipe(
                db,
                "Garlic Butter Pasta",
                "Cook pasta. Fry garlic in butter and mix with the pasta.",
                new String[]{"Pasta", "Garlic", "Butter"},
                new double[]{200, 15, 30},
                new String[]{"g", "g", "g"}
        );

        // Recipe 18
        addSeedRecipe(
                db,
                "Creamy Chicken Pasta",
                "Cook chicken and pasta. Add milk and cheese and stir " +
                        "until creamy.",
                new String[]{"Chicken", "Pasta", "Milk", "Cheese"},
                new double[]{200, 200, 150, 50},
                new String[]{"g", "g", "ml", "g"}
        );

        // Recipe 19
        addSeedRecipe(
                db,
                "Tuna Egg Sandwich",
                "Mix tuna and chopped boiled egg. Place between bread.",
                new String[]{"Tuna", "Egg", "Bread"},
                new double[]{100, 2, 2},
                new String[]{"g", "pieces", "slices"}
        );

        // Recipe 20
        addSeedRecipe(
                db,
                "Chicken Vegetable Rice",
                "Cook chicken and rice. Stir-fry carrot, onion and " +
                        "chicken, then add rice.",
                new String[]{"Chicken", "Rice", "Carrot", "Onion"},
                new double[]{200, 300, 100, 1},
                new String[]{"g", "g", "g", "pieces"}
        );
    }

    private void addSeedRecipe(
            SQLiteDatabase db,
            String recipeName,
            String instructions,
            String[] ingredientNames,
            double[] quantities,
            String[] units) {

        android.content.ContentValues recipeValues =
                new android.content.ContentValues();

        recipeValues.put("name", recipeName);
        recipeValues.put("instructions", instructions);

        long recipeId = db.insert(
                "recipes",
                null,
                recipeValues
        );

        if (recipeId != -1) {

            for (int i = 0; i < ingredientNames.length; i++) {

                android.content.ContentValues ingredientValues =
                        new android.content.ContentValues();

                ingredientValues.put(
                        "recipe_id",
                        recipeId
                );

                ingredientValues.put(
                        "ingredient_name",
                        ingredientNames[i]
                );

                ingredientValues.put(
                        "quantity",
                        quantities[i]
                );

                ingredientValues.put(
                        "unit",
                        units[i]
                );

                db.insert(
                        "recipe_ingredients",
                        null,
                        ingredientValues
                );
            }
        }
    }

}