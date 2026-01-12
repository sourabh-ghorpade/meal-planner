# Quickstart: Meal Planning Feature

**Feature**: 001-meal-planning
**Date**: 2026-01-11

## Prerequisites

Before running the Meal Planning feature, ensure:

1. **Android Studio**: Hedgehog (2023.1.1) or later
2. **JDK**: 17 or later (bundled with Android Studio)
3. **Android SDK**: API 24-36 installed
4. **Emulator or Device**: Android 7.0+ device or emulator

## Build & Run

### 1. Clone and Open

```bash
git clone <repository-url>
cd MealPlanner
git checkout 001-meal-planning
```

Open in Android Studio: **File > Open** and select the `MealPlanner` directory.

### 2. Sync Gradle

Android Studio should auto-sync. If not:
- **File > Sync Project with Gradle Files**
- Or click the elephant icon in the toolbar

### 3. Run the App

1. Select a device/emulator from the device dropdown
2. Click the green **Run** button (or `Shift+F10`)
3. Wait for the app to install and launch

## Verify Feature Works

### Test 1: View Week

1. Launch the app
2. Navigate to the Meal Planning screen
3. **Expected**: See a 7-day week view with breakfast, lunch, dinner slots for each day

### Test 2: Navigate Weeks

1. Tap the right arrow (▶) to go to next week
2. **Expected**: Dates update to show next week
3. Tap the left arrow (◀) to go back
4. **Expected**: Returns to previous week
5. Navigate away and tap "Today" button
6. **Expected**: Returns to current week with today highlighted

### Test 3: Schedule a Meal

1. (First, ensure at least one meal exists - see Database Seeding below)
2. Tap on an empty breakfast slot for any day
3. **Expected**: Bottom sheet appears with list of meals
4. Select a meal from the list
5. **Expected**: Bottom sheet closes, meal appears in the slot

### Test 4: Edit a Meal

1. Tap on a slot that already has a meal
2. **Expected**: Bottom sheet appears with meal options
3. Select a different meal
4. **Expected**: Slot now shows the new meal

### Test 5: Remove a Meal

1. Long-press (or swipe) on a scheduled meal
2. **Expected**: Remove option appears
3. Confirm removal
4. **Expected**: Slot returns to empty state

### Test 6: Data Persistence

1. Schedule a few meals across different days
2. Force close the app (swipe from recents)
3. Relaunch the app
4. **Expected**: All scheduled meals are still in place

## Database Seeding

For testing, the app needs some meals to schedule. During development, you can:

### Option A: Prepopulated Database

The app includes a prepopulated database callback with sample meals:

```kotlin
// In MealPlannerDatabase.kt
.addCallback(object : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Insert sample meals
        db.execSQL("INSERT INTO meals (name) VALUES ('Oatmeal with Berries')")
        db.execSQL("INSERT INTO meals (name) VALUES ('Scrambled Eggs')")
        db.execSQL("INSERT INTO meals (name) VALUES ('Avocado Toast')")
        db.execSQL("INSERT INTO meals (name) VALUES ('Grilled Chicken Salad')")
        db.execSQL("INSERT INTO meals (name) VALUES ('Turkey Sandwich')")
        db.execSQL("INSERT INTO meals (name) VALUES ('Pasta Primavera')")
        db.execSQL("INSERT INTO meals (name) VALUES ('Salmon with Vegetables')")
        db.execSQL("INSERT INTO meals (name) VALUES ('Stir Fry')")
    }
})
```

### Option B: Database Inspector

1. Run the app on emulator
2. In Android Studio: **View > Tool Windows > App Inspection**
3. Select **Database Inspector**
4. Insert meals directly into the `meals` table

## Running Tests

### Unit Tests

```bash
./gradlew testDebugUnitTest
```

Tests located in: `app/src/test/java/com/example/mealplanner/`

### Instrumented Tests

```bash
./gradlew connectedDebugAndroidTest
```

Requires connected device or running emulator.
Tests located in: `app/src/androidTest/java/com/example/mealplanner/`

### Specific Test Class

```bash
./gradlew testDebugUnitTest --tests "com.example.mealplanner.mealplan.MealPlanViewModelTest"
```

## Troubleshooting

### Issue: "Cannot find meal plan screen"

**Cause**: Navigation not yet implemented
**Solution**: Add MealPlanScreen to the main navigation graph or set it as the start destination

### Issue: "No meals available"

**Cause**: Database is empty
**Solution**: See Database Seeding section above

### Issue: "Week starts on wrong day"

**Cause**: Locale-dependent week start
**Solution**: The app uses device locale. To test different locales:
1. Device Settings > Language
2. Change to desired locale
3. Restart app

### Issue: Build fails with "Unsupported class version"

**Cause**: JDK version mismatch
**Solution**: Ensure Gradle JDK is set to 17+
- **File > Settings > Build > Build Tools > Gradle**
- Set Gradle JDK to 17 or bundled JDK

## Feature Flags

None for this feature. All functionality is enabled by default.

## Architecture Notes

```
UI Layer (Compose)          ViewModel              Data Layer (Room)
─────────────────          ─────────              ─────────────────
MealPlanScreen      ──►    MealPlanViewModel ──►  ScheduledMealDao
├── WeekView               │                      MealDao
├── DayColumn              ├── UiState            │
├── MealSlotCard           └── Actions            ▼
└── MealPickerBottomSheet                     SQLite Database
```

- **Unidirectional data flow**: UI observes StateFlow, emits events to ViewModel
- **No repository layer**: Direct DAO access per constitution (local-only data)
- **Room with Flow**: Reactive data updates propagate to UI automatically
