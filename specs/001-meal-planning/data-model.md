# Data Model: Meal Planning

**Feature**: 001-meal-planning
**Date**: 2026-01-11

## Overview

This document defines the data entities, their relationships, and validation rules for the Meal Planning feature.

## Entities

### MealSlotType (Enum)

Represents the time-of-day category for a meal.

| Value | Display Name | Order |
|-------|--------------|-------|
| BREAKFAST | Breakfast | 0 |
| LUNCH | Lunch | 1 |
| DINNER | Dinner | 2 |

**Validation**: Fixed set, no user-defined slots in this version.

---

### Meal

A food item that can be scheduled in the meal planner.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | Primary key, auto-generated | Unique identifier |
| name | String | Not blank, max 100 chars | Display name of the meal |

**Validation Rules**:
- `name` MUST NOT be empty or whitespace-only
- `name` MUST NOT exceed 100 characters
- Duplicate names are allowed (user may have "Pasta" multiple times with different meanings)

**State Transitions**: None (simple CRUD entity)

---

### ScheduledMeal

An assignment of a meal to a specific date and time slot.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | Primary key, auto-generated | Unique identifier |
| mealId | Long | Foreign key → Meal.id | Reference to the scheduled meal |
| date | LocalDate | Not null | The calendar date for this meal |
| slotType | MealSlotType | Not null | Which slot (breakfast/lunch/dinner) |

**Validation Rules**:
- `mealId` MUST reference an existing Meal
- `date` MUST be a valid calendar date
- `slotType` MUST be a valid enum value
- Unique constraint on (`date`, `slotType`) - only one meal per slot per day

**State Transitions**:
```
[Empty Slot] --assign meal--> [Scheduled]
[Scheduled] --replace meal--> [Scheduled (different meal)]
[Scheduled] --remove--> [Empty Slot]
```

---

## Relationships

```
┌─────────┐         ┌────────────────┐
│  Meal   │ 1 ───── * │ ScheduledMeal  │
│         │           │                │
│ id (PK) │◄──────────│ mealId (FK)    │
│ name    │           │ date           │
│         │           │ slotType       │
└─────────┘           │                │
                      │ UNIQUE(date,   │
                      │  slotType)     │
                      └────────────────┘
```

**Cardinality**:
- One Meal can be scheduled many times (different dates/slots)
- One ScheduledMeal references exactly one Meal
- Each (date, slotType) combination has at most one ScheduledMeal

---

## Database Schema (Room)

### MealEntity

```kotlin
@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String
)
```

### ScheduledMealEntity

```kotlin
@Entity(
    tableName = "scheduled_meals",
    foreignKeys = [
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["id"],
            childColumns = ["meal_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["date"]),
        Index(value = ["date", "slot_type"], unique = true)
    ]
)
data class ScheduledMealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "meal_id")
    val mealId: Long,

    @ColumnInfo(name = "date")
    val date: Long,  // Epoch day (LocalDate.toEpochDay())

    @ColumnInfo(name = "slot_type")
    val slotType: String  // MealSlotType.name
)
```

---

## Domain Models

Domain models are separate from database entities for clean separation.

### Meal (Domain)

```kotlin
data class Meal(
    val id: Long,
    val name: String
)
```

### ScheduledMeal (Domain)

```kotlin
data class ScheduledMeal(
    val id: Long,
    val meal: Meal,
    val date: LocalDate,
    val slotType: MealSlotType
)
```

---

## Type Converters

Room requires converters for non-primitive types:

```kotlin
class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate): Long = date.toEpochDay()

    @TypeConverter
    fun toLocalDate(epochDay: Long): LocalDate = LocalDate.ofEpochDay(epochDay)

    @TypeConverter
    fun fromMealSlotType(slotType: MealSlotType): String = slotType.name

    @TypeConverter
    fun toMealSlotType(value: String): MealSlotType = MealSlotType.valueOf(value)
}
```

---

## Queries

### Get Scheduled Meals for Week

```sql
SELECT sm.*, m.name as meal_name
FROM scheduled_meals sm
INNER JOIN meals m ON sm.meal_id = m.id
WHERE sm.date >= :weekStartEpochDay
  AND sm.date <= :weekEndEpochDay
ORDER BY sm.date, sm.slot_type
```

### Get All Meals (for picker)

```sql
SELECT * FROM meals ORDER BY name ASC
```

### Upsert Scheduled Meal

```sql
INSERT OR REPLACE INTO scheduled_meals (meal_id, date, slot_type)
VALUES (:mealId, :date, :slotType)
```

### Delete Scheduled Meal

```sql
DELETE FROM scheduled_meals WHERE date = :date AND slot_type = :slotType
```
