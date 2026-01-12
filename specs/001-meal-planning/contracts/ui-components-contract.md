# UI Components Contract: Meal Planning

**Feature**: 001-meal-planning
**Date**: 2026-01-11

## Overview

This document defines the Compose UI component contracts for the Meal Planning feature. Each component specifies its inputs (parameters), outputs (callbacks), and visual behavior.

---

## MealPlanScreen

Top-level screen composable that orchestrates the meal planning UI.

### Signature

```kotlin
@Composable
fun MealPlanScreen(
    viewModel: MealPlanViewModel = viewModel()
)
```

### Behavior

- Collects state from ViewModel as `collectAsStateWithLifecycle()`
- Displays `WeekView` with navigation controls
- Shows `MealPickerBottomSheet` when `selectedSlot` is non-null
- Displays loading indicator when `isLoading` is true
- Displays error snackbar when `error` is non-null

### Preview

```kotlin
@Preview
@Composable
fun MealPlanScreenPreview() {
    // Preview with sample state
}
```

---

## WeekView

Displays a 7-day week with navigation and meal slots.

### Signature

```kotlin
@Composable
fun WeekView(
    weekStart: LocalDate,
    days: List<DayWithMeals>,
    onNavigateWeek: (offset: Int) -> Unit,
    onGoToToday: () -> Unit,
    onSlotClick: (date: LocalDate, slotType: MealSlotType) -> Unit,
    onMealRemove: (date: LocalDate, slotType: MealSlotType) -> Unit,
    modifier: Modifier = Modifier
)
```

### Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| weekStart | LocalDate | First day of the displayed week |
| days | List<DayWithMeals> | 7 days with their scheduled meals |
| onNavigateWeek | (Int) -> Unit | Called with +1 (next) or -1 (prev) |
| onGoToToday | () -> Unit | Called when "Today" button tapped |
| onSlotClick | (LocalDate, MealSlotType) -> Unit | Called when empty or filled slot tapped |
| onMealRemove | (LocalDate, MealSlotType) -> Unit | Called when remove action triggered |

### Visual Structure

```
┌─────────────────────────────────────────┐
│  ◀  January 6 - January 12, 2026  ▶ [T] │  <- Header with nav + Today button
├─────────────────────────────────────────┤
│ Mon  │ Tue  │ Wed  │ Thu  │ Fri  │ ...  │  <- Day headers (scrollable)
├──────┼──────┼──────┼──────┼──────┼──────┤
│ 🍳   │ 🍳   │  +   │ 🍳   │  +   │      │  <- Breakfast row
│ 🥗   │  +   │ 🥗   │  +   │ 🥗   │      │  <- Lunch row
│ 🍝   │ 🍝   │ 🍝   │  +   │ 🍝   │      │  <- Dinner row
└──────┴──────┴──────┴──────┴──────┴──────┘
```

### Preview

```kotlin
@Preview
@Composable
fun WeekViewPreview() {
    WeekView(
        weekStart = LocalDate.of(2026, 1, 6),
        days = sampleDays(),
        onNavigateWeek = {},
        onGoToToday = {},
        onSlotClick = { _, _ -> },
        onMealRemove = { _, _ -> }
    )
}
```

---

## DayColumn

A single day's column showing the date and three meal slots.

### Signature

```kotlin
@Composable
fun DayColumn(
    day: DayWithMeals,
    onSlotClick: (MealSlotType) -> Unit,
    onMealRemove: (MealSlotType) -> Unit,
    modifier: Modifier = Modifier
)
```

### Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| day | DayWithMeals | The day's data including date and slots |
| onSlotClick | (MealSlotType) -> Unit | Called when slot tapped |
| onMealRemove | (MealSlotType) -> Unit | Called when remove triggered |

### Visual Indicators

- `isToday`: Highlighted background or border
- `isPast`: Slightly dimmed or different styling

---

## MealSlotCard

A single meal slot showing either an assigned meal or empty state.

### Signature

```kotlin
@Composable
fun MealSlotCard(
    slotType: MealSlotType,
    meal: Meal?,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
)
```

### Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| slotType | MealSlotType | BREAKFAST, LUNCH, or DINNER |
| meal | Meal? | Assigned meal or null if empty |
| onClick | () -> Unit | Called when card tapped |
| onRemove | () -> Unit | Called when remove action triggered |

### States

1. **Empty**: Shows slot type icon + "+" indicator, tap to add
2. **Filled**: Shows meal name, tap to replace, long-press or swipe to remove

### Preview

```kotlin
@Preview
@Composable
fun MealSlotCardEmptyPreview() {
    MealSlotCard(
        slotType = MealSlotType.BREAKFAST,
        meal = null,
        onClick = {},
        onRemove = {}
    )
}

@Preview
@Composable
fun MealSlotCardFilledPreview() {
    MealSlotCard(
        slotType = MealSlotType.LUNCH,
        meal = Meal(1, "Grilled Chicken Salad"),
        onClick = {},
        onRemove = {}
    )
}
```

---

## MealPickerBottomSheet

Modal bottom sheet for selecting a meal to assign.

### Signature

```kotlin
@Composable
fun MealPickerBottomSheet(
    meals: List<Meal>,
    selectedSlot: SelectedSlot,
    onMealSelected: (Meal) -> Unit,
    onDismiss: () -> Unit
)
```

### Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| meals | List<Meal> | Available meals to choose from |
| selectedSlot | SelectedSlot | Context showing which slot is being filled |
| onMealSelected | (Meal) -> Unit | Called when meal tapped |
| onDismiss | () -> Unit | Called when sheet dismissed |

### Visual Structure

```
┌─────────────────────────────────┐
│ Select Meal for                 │
│ Monday Breakfast                │  <- Shows context
├─────────────────────────────────┤
│ 🔍 Search meals...              │  <- Optional: search filter
├─────────────────────────────────┤
│ ○ Oatmeal with Berries          │
│ ○ Scrambled Eggs                │
│ ○ Avocado Toast                 │  <- Scrollable list
│ ○ Greek Yogurt Parfait          │
│ ...                             │
└─────────────────────────────────┘
```

### Empty State

When `meals` is empty:
```
┌─────────────────────────────────┐
│         No meals yet            │
│                                 │
│    Add some meals to get        │
│    started with planning        │
│                                 │
│    [Add Meal] (future feature)  │
└─────────────────────────────────┘
```

---

## EmptyState

Reusable empty state component for various scenarios.

### Signature

```kotlin
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    action: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
)
```

### Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| icon | ImageVector | Illustrative icon |
| title | String | Primary message |
| description | String | Secondary explanation |
| action | Composable? | Optional action button |

### Usage Examples

```kotlin
// No meals to schedule
EmptyState(
    icon = Icons.Outlined.Restaurant,
    title = "No meals available",
    description = "Add some meals to start planning your week"
)

// Empty week
EmptyState(
    icon = Icons.Outlined.CalendarToday,
    title = "No meals planned",
    description = "Tap a slot to add your first meal"
)
```

---

## Accessibility Requirements

All components MUST:
- Have `contentDescription` for icons and images
- Support TalkBack navigation
- Use semantic roles (`Role.Button`, etc.) appropriately
- Meet minimum touch target size (48dp)
- Support dynamic text scaling
