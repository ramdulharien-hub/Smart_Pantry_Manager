# Smart Pantry Manager

## App Description

Smart Pantry Manager is an Android application designed to help users manage pantry ingredients and discover recipes based on the ingredients they currently have available.

The application provides a simple way to add, edit, view, and delete pantry ingredients while storing the information locally on the device. Users can also browse a collection of recipes, view complete recipe details, and receive recipe suggestions based on the ingredients currently stored in their pantry.

The application also includes expiry-date management and expiry alerts to help users keep track of pantry items that are approaching their expiry dates.

---

## Key Features

### Pantry Management

Users can manage their pantry ingredients through the application.

- Add new pantry ingredients
- Edit existing pantry ingredients
- Delete pantry ingredients
- View all stored pantry ingredients
- Enter ingredient name
- Enter quantity
- Select or enter the measurement unit
- Enter an optional expiry date
- Store pantry information locally using SQLite

### Recipe Management

The application contains a collection of 20 seeded recipes stored in the local database.

Users can:

- Browse available recipes
- View recipe names
- View required ingredients
- View preparation instructions
- Open a recipe to view its full details

### Suggested Recipes

The application provides recipe suggestions based on the ingredients stored in the user's pantry.

Recipe suggestions use strict ingredient matching. A recipe is suggested only when the ingredients required by the recipe are available in the pantry.

If there are no recipes that can currently be prepared using the available pantry ingredients, the application provides feedback to the user instead of displaying an empty or misleading result.

### Expiry Alerts

Pantry ingredients can have an optional expiry date.

The application supports expiry notifications to help users keep track of ingredients that are approaching their expiry date.

Expiry alerts can be:

- Enabled or disabled from the Settings screen
- Scheduled automatically when an ingredient is added
- Rescheduled when an ingredient is edited
- Cancelled when an ingredient is deleted
- Cancelled when pantry data is cleared

The application requests notification permission where required by the Android version running on the device.

### Settings

The Settings screen provides application preferences and management options.

Current settings include:

- Dark Mode
- Expiry Soon Alerts
- Clear Pantry Data
- Return to the previous screen

Dark Mode and expiry-alert preferences are stored using Android `SharedPreferences` so that the selected settings can persist.

---

## Database

The application uses **SQLite** as its local database.

SQLite was chosen because it provides a lightweight, reliable local database solution that is well suited to an Android application. It allows pantry ingredients and recipe information to be stored directly on the user's device without requiring an external database server.

The database is managed using Android's `SQLiteOpenHelper` class.

### Database Tables

The application uses the following main database tables:

#### Pantry Items

Stores the ingredients currently available in the user's pantry.

Information includes:

- Item ID
- Ingredient name
- Quantity
- Unit
- Expiry date

#### Recipes

Stores the recipes available in the application.

Information includes:

- Recipe ID
- Recipe name
- Preparation steps

#### Recipe Ingredients

Stores the ingredients required by each recipe.

This allows recipes to be associated with their required ingredients and enables the application to compare recipe requirements with the ingredients stored in the pantry.

---

## Recipe Matching

The Suggested Recipes feature compares the ingredients required by each recipe with the ingredients currently stored in the pantry.

The application uses strict matching so that recipes are only suggested when the required ingredients are available.

This provides users with recipes that they can currently prepare based on their stored pantry information.

If no recipe matches the available ingredients, the application displays feedback informing the user that there are currently no matching recipes.

---

## Expiry Notification System

Expiry notifications are implemented using Android's notification and alarm functionality.

When a pantry item has an expiry date, the application can schedule an expiry alert.

The notification system includes:

- Notification channel creation
- Expiry alert scheduling
- Expiry alert cancellation
- Notification permission handling on supported Android versions
- Rescheduling of alerts when pantry items are edited
- Cancellation of alerts when pantry items are deleted
- Ability to enable or disable expiry alerts from Settings

The notification system uses the pantry item's database ID so that individual alerts can be managed correctly.

---

## User Interface

The application contains several screens for different parts of the pantry and recipe management process.

### Main Screen

The main screen allows users to:

- View pantry ingredients
- Add ingredients
- Edit ingredients
- Delete ingredients
- Navigate to suggested recipes
- Navigate to settings

Pantry items are displayed using a RecyclerView.

### Add/Edit Ingredient Screen

This screen allows users to enter or modify:

- Ingredient name
- Quantity
- Unit
- Expiry date

The same screen is used for both adding new ingredients and editing existing ingredients.

### Suggested Recipes Screen

This screen displays recipes that can be prepared using the ingredients currently available in the pantry.

### Recipe Detail Screen

This screen displays:

- Recipe name
- Full ingredient list
- Preparation method/instructions

### Settings Screen

The Settings screen allows users to manage:

- Dark Mode
- Expiry Soon Alerts
- Pantry data

---

## Technologies Used

- **Android Studio** – Development environment
- **Java** – Application programming language
- **XML** – User interface layouts
- **SQLite** – Local database
- **SQLiteOpenHelper** – Database management
- **RecyclerView** – Displaying pantry and recipe lists
- **SharedPreferences** – Storing user settings
- **Android Notifications** – Expiry alerts
- **AlarmManager** – Scheduling expiry alerts
- **Gradle** – Project build system

---

## Project Structure

The project is organised into Android activities, layouts, database classes, adapters, and notification components.


<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/59d80129-14d8-46fe-bfd3-13fd9ce1ffde" />

