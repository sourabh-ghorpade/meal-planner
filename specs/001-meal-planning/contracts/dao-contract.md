# DAO Contract: Meal Planning Database Access

**Feature**: 001-meal-planning
**Date**: 2026-01-11

## Overview

This document defines the Room DAO interfaces for the Meal Planning feature. These contracts specify the database operations available to the ViewModel.

---

## MealDao

Access to the `meals` table.

### getAllMeals

Get all meals ordered alphabetically for the meal picker.

```kotlin
@Query("SELECT * FROM meals ORDER BY name ASC")
fun getAllMeals(): Flow<List<MealEntity>>
```

**Returns**: Flow of all meals, emits on any change to meals table

---

### getMealById

Get a single meal by ID.

```kotlin
@Query("SELECT * FROM meals WHERE id = :id")
suspend fun getMealById(id: Long): MealEntity?
```

**Returns**: The meal entity or null if not found

---

### insertMeal

Insert a new meal.

```kotlin
@Insert
suspend fun insertMeal(meal: MealEntity): Long
```

**Returns**: The auto-generated ID of the inserted meal

---

### deleteMeal

Delete a meal by ID. Cascades to scheduled_meals.

```kotlin
@Query("DELETE FROM meals WHERE id = :id")
suspend fun deleteMeal(id: Long)
```

**Side Effects**: All ScheduledMeal entries referencing this meal are deleted (CASCADE)

---

## ScheduledMealDao

Access to the `scheduled_meals` table.

### getScheduledMealsForWeek

Get all scheduled meals within a date range, joined with meal data.

```kotlin
@Query("""
    SELECT sm.id, sm.date, sm.slot_type, m.id as meal_id, m.name as meal_name
    FROM scheduled_meals sm
    INNER JOIN meals m ON sm.meal_id = m.id
    WHERE sm.date >= :startDate AND sm.date <= :endDate
    ORDER BY sm.date, sm.slot_type
""")
fun getScheduledMealsForWeek(startDate: Long, endDate: Long): Flow<List<ScheduledMealWithMeal>>
```

**Parameters**:
- `startDate`: Epoch day of week start (inclusive)
- `endDate`: Epoch day of week end (inclusive)

**Returns**: Flow of scheduled meals with meal details, emits on changes

---

### upsertScheduledMeal

Insert or replace a scheduled meal for a specific date and slot.

```kotlin
@Query("""
    INSERT OR REPLACE INTO scheduled_meals (meal_id, date, slot_type)
    VALUES (:mealId, :date, :slotType)
""")
suspend fun upsertScheduledMeal(mealId: Long, date: Long, slotType: String)
```

**Parameters**:
- `mealId`: ID of the meal to schedule
- `date`: Epoch day
- `slotType`: MealSlotType enum name (BREAKFAST/LUNCH/DINNER)

**Behavior**: If entry exists for (date, slotType), replaces meal_id

---

### deleteScheduledMeal

Remove a scheduled meal from a specific slot.

```kotlin
@Query("DELETE FROM scheduled_meals WHERE date = :date AND slot_type = :slotType")
suspend fun deleteScheduledMeal(date: Long, slotType: String)
```

**Parameters**:
- `date`: Epoch day
- `slotType`: MealSlotType enum name

---

### deleteAllScheduledMeals

Clear all scheduled meals (for testing or reset).

```kotlin
@Query("DELETE FROM scheduled_meals")
suspend fun deleteAllScheduledMeals()
```

---

## Data Transfer Objects

### ScheduledMealWithMeal

Result of joined query combining scheduled_meals and meals tables.

```kotlin
data class ScheduledMealWithMeal(
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "date")
    val date: Long,

    @ColumnInfo(name = "slot_type")
    val slotType: String,

    @ColumnInfo(name = "meal_id")
    val mealId: Long,

    @ColumnInfo(name = "meal_name")
    val mealName: String
)
```

**Mapping to Domain**:
```kotlin
fun ScheduledMealWithMeal.toDomain(): ScheduledMeal = ScheduledMeal(
    id = id,
    meal = Meal(id = mealId, name = mealName),
    date = LocalDate.ofEpochDay(date),
    slotType = MealSlotType.valueOf(slotType)
)
```

---

## Database Definition

### MealPlannerDatabase

```kotlin
@Database(
    entities = [MealEntity::class, ScheduledMealEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MealPlannerDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun scheduledMealDao(): ScheduledMealDao
}
```

**Migration Policy**:
- Version 1 is initial schema
- Future migrations will use Room's Migration API
- Schema exported for version control

---

## Performance Expectations

| Operation | Expected Time | Notes |
|-----------|---------------|-------|
| getAllMeals | <50ms | Initial load, then reactive |
| getScheduledMealsForWeek | <50ms | Indexed on date |
| upsertScheduledMeal | <20ms | Single row operation |
| deleteScheduledMeal | <20ms | Single row operation |

All operations are non-blocking (suspend functions or Flow).
