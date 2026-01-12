# Research: Meal Planning Feature

**Feature**: 001-meal-planning
**Date**: 2026-01-11

## Overview

This document captures technical research and decisions for the Meal Planning feature. Most decisions are predetermined by the project constitution; this document confirms alignment and documents specific implementation patterns.

## Decision 1: Local Persistence Strategy

**Decision**: Use Room database with simple entity structure

**Rationale**:
- Constitution mandates AndroidX libraries over third-party alternatives
- Room provides compile-time SQL verification and Flow support for reactive updates
- Local-only storage per spec assumptions (no cloud sync)
- Room integrates seamlessly with Kotlin coroutines and Compose

**Alternatives Considered**:
- **DataStore**: Too limited for relational data (meal-to-schedule relationships)
- **SQLite directly**: More boilerplate, no compile-time safety
- **Third-party ORM (Realm, ObjectBox)**: Violates constitution's AndroidX preference

## Decision 2: Week Navigation Implementation

**Decision**: Use `java.time.LocalDate` with week calculation helper functions

**Rationale**:
- `java.time` available on API 26+; use desugaring for API 24-25 compatibility
- `LocalDate` handles week boundaries, year transitions automatically
- Simple functions: `getWeekStartDate(date)`, `getWeekEndDate(date)`, `navigateWeek(offset)`

**Alternatives Considered**:
- **Calendar API**: More verbose, mutable (not idiomatic Kotlin)
- **ThreeTenABP**: Unnecessary with core library desugaring enabled
- **Custom date math**: Error-prone, reinventing the wheel

**Implementation Notes**:
- Week start day follows device locale (Sunday in US, Monday in EU)
- Use `WeekFields.of(Locale.getDefault())` for locale-aware week boundaries

## Decision 3: State Management Pattern

**Decision**: ViewModel with StateFlow, direct DAO access

**Rationale**:
- Constitution's Compose-First principle requires unidirectional data flow
- StateFlow provides lifecycle-aware, composable-friendly state emission
- No repository layer needed (YAGNI - local-only data, single source)
- ViewModel survives configuration changes

**Alternatives Considered**:
- **Repository pattern**: Over-engineering for local-only data with no remote source
- **LiveData**: StateFlow preferred for Compose (better null safety, operators)
- **MVI with Redux-like store**: Violates simplicity principle

**State Structure**:
```kotlin
data class MealPlanUiState(
    val currentWeekStart: LocalDate,
    val scheduledMeals: Map<LocalDate, Map<MealSlotType, Meal?>>,
    val availableMeals: List<Meal>,
    val isLoading: Boolean,
    val selectedSlot: Pair<LocalDate, MealSlotType>? // for meal picker
)
```

## Decision 4: Meal Slot Type Modeling

**Decision**: Enum with fixed slots (BREAKFAST, LUNCH, DINNER)

**Rationale**:
- Spec explicitly states 3 fixed slots; custom slots out of scope
- Enum provides type safety and exhaustive when expressions
- Simple display order via enum ordinal

**Alternatives Considered**:
- **Sealed class**: No additional data needed per slot type
- **String constants**: No type safety, error-prone
- **Database-driven dynamic slots**: Violates YAGNI (out of scope)

```kotlin
enum class MealSlotType {
    BREAKFAST,
    LUNCH,
    DINNER
}
```

## Decision 5: Meal Picker UI Pattern

**Decision**: Bottom sheet with scrollable meal list

**Rationale**:
- Bottom sheet is native Android pattern, thumb-friendly
- Material 3 provides `ModalBottomSheet` composable
- Allows quick selection without full-screen navigation
- Can show empty state guidance when no meals exist

**Alternatives Considered**:
- **Full-screen dialog**: Too heavy for simple list selection
- **Dropdown menu**: Poor UX for potentially long meal lists
- **Inline expansion**: Clutters the week view layout

## Decision 6: Empty State Handling

**Decision**: Contextual empty states with action guidance

**Rationale**:
- Spec requires "empty state with guidance when no meals available" (FR-009)
- Two empty state scenarios:
  1. No meals in system → Guide to add meals (future feature)
  2. No meals scheduled for week → Encourage adding first meal

**Implementation**:
- Reusable `EmptyState` composable in `ui/components/`
- Accepts icon, title, description, optional action button

## Decision 7: Database Schema

**Decision**: Two tables with foreign key relationship

**Rationale**:
- `Meal` table for reusable meal definitions
- `ScheduledMeal` table links meal to date + slot
- Foreign key ensures referential integrity
- Cascade delete when meal removed (or null if allowing orphan schedules)

**Schema**:
```
meals
├── id: Long (PK, auto-generate)
└── name: String (not null)

scheduled_meals
├── id: Long (PK, auto-generate)
├── meal_id: Long (FK → meals.id)
├── date: Long (epoch day, indexed)
└── slot_type: String (enum name)
```

**Indexes**:
- `scheduled_meals(date)` for efficient week queries

## Open Items

None. All technical decisions resolved through constitution guidance and standard Android patterns.

## References

- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [StateFlow and SharedFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
- [Material 3 Bottom Sheets](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#ModalBottomSheet)
- [Core Library Desugaring](https://developer.android.com/studio/write/java8-support#library-desugaring)
