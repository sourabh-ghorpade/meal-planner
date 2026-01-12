# ViewModel Contract: MealPlanViewModel

**Feature**: 001-meal-planning
**Date**: 2026-01-11

## Overview

This document defines the contract between the UI layer and the MealPlanViewModel. It specifies the state shape, available actions, and expected behaviors.

## State

### MealPlanUiState

```kotlin
data class MealPlanUiState(
    val currentWeekStart: LocalDate,
    val weekDays: List<DayWithMeals>,
    val availableMeals: List<Meal>,
    val isLoading: Boolean,
    val selectedSlot: SelectedSlot?,
    val error: String?
)

data class DayWithMeals(
    val date: LocalDate,
    val isPast: Boolean,
    val isToday: Boolean,
    val slots: Map<MealSlotType, Meal?>
)

data class SelectedSlot(
    val date: LocalDate,
    val slotType: MealSlotType
)
```

### Initial State

```kotlin
MealPlanUiState(
    currentWeekStart = LocalDate.now().with(weekStartField),
    weekDays = emptyList(),  // Populated on first load
    availableMeals = emptyList(),
    isLoading = true,
    selectedSlot = null,
    error = null
)
```

## Actions (User Intents)

### NavigateToWeek

Move to a different week.

| Parameter | Type | Description |
|-----------|------|-------------|
| offset | Int | Weeks to navigate (+1 = next, -1 = previous) |

**Behavior**:
1. Update `currentWeekStart` by adding `offset` weeks
2. Load scheduled meals for the new week range
3. Update `weekDays` with new data

**Postconditions**:
- `currentWeekStart` reflects the new week
- `weekDays` contains 7 days starting from new `currentWeekStart`
- Previous week's data is cleared from state (not cached)

---

### GoToCurrentWeek

Jump directly to the week containing today.

**Behavior**:
1. Calculate week start for today's date
2. If already on current week, no-op
3. Otherwise, equivalent to `NavigateToWeek` with appropriate offset

**Postconditions**:
- `currentWeekStart` is the Monday/Sunday of the current week
- One of the `weekDays` has `isToday = true`

---

### SelectSlot

Open the meal picker for a specific slot.

| Parameter | Type | Description |
|-----------|------|-------------|
| date | LocalDate | The day to schedule |
| slotType | MealSlotType | Which slot to fill |

**Behavior**:
1. Set `selectedSlot` to the given date and slot
2. UI layer shows meal picker (bottom sheet)

**Postconditions**:
- `selectedSlot` is non-null
- Available meals are loaded if not already

---

### AssignMeal

Schedule a meal to the currently selected slot.

| Parameter | Type | Description |
|-----------|------|-------------|
| meal | Meal | The meal to assign |

**Preconditions**:
- `selectedSlot` is non-null

**Behavior**:
1. Insert or replace ScheduledMeal in database
2. Update `weekDays` to reflect the new assignment
3. Clear `selectedSlot` (dismiss picker)

**Postconditions**:
- The slot now contains the assigned meal
- `selectedSlot` is null
- Database is updated

---

### RemoveMeal

Remove a meal from a slot.

| Parameter | Type | Description |
|-----------|------|-------------|
| date | LocalDate | The day |
| slotType | MealSlotType | Which slot to clear |

**Behavior**:
1. Delete ScheduledMeal from database where date and slotType match
2. Update `weekDays` to show empty slot

**Postconditions**:
- The slot shows as empty (null meal)
- Database record deleted

---

### DismissPicker

Close the meal picker without making a selection.

**Behavior**:
1. Set `selectedSlot` to null

**Postconditions**:
- `selectedSlot` is null
- No database changes

---

### ClearError

Dismiss any displayed error message.

**Behavior**:
1. Set `error` to null

---

## State Flow Diagram

```
                    ┌──────────────────┐
                    │   Initial Load   │
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
              ┌─────│   Week Loaded    │◄────────┐
              │     └────────┬─────────┘         │
              │              │                   │
    NavigateToWeek      SelectSlot          GoToCurrentWeek
              │              │                   │
              │              ▼                   │
              │     ┌──────────────────┐         │
              │     │  Picker Open     │         │
              │     └────────┬─────────┘         │
              │              │                   │
              │    ┌─────────┼─────────┐         │
              │    │         │         │         │
              │ AssignMeal  │    DismissPicker   │
              │    │   RemoveMeal      │         │
              │    │         │         │         │
              │    └─────────┼─────────┘         │
              │              │                   │
              └──────────────┴───────────────────┘
```

## Error Handling

| Scenario | Error Message | Recovery Action |
|----------|---------------|-----------------|
| Database read failure | "Unable to load meal plan. Please try again." | Retry load |
| Database write failure | "Unable to save changes. Please try again." | Retry action |
| No meals available | null (show empty state instead) | N/A |

## Threading

- All database operations run on `Dispatchers.IO`
- State updates posted to `Dispatchers.Main`
- UI observes `StateFlow` and recomposes automatically
