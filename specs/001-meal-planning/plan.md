# Implementation Plan: Meal Planning

**Branch**: `001-meal-planning` | **Date**: 2026-01-11 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-meal-planning/spec.md`

## Summary

Implement a weekly meal planning feature that allows users to view a 7-day calendar with breakfast, lunch, and dinner slots. Users can assign meals to slots, navigate between weeks, and edit/remove scheduled meals. Data persists locally using Room database with a simple data model: Meal → ScheduledMeal (date + slot type).

## Technical Context

**Language/Version**: Kotlin (JVM 11)
**Primary Dependencies**: Jetpack Compose, Material 3, Room (local persistence), AndroidX Lifecycle
**Storage**: Room database (local SQLite) for meal and schedule persistence
**Testing**: JUnit 4 for unit tests, Compose Testing for UI tests
**Target Platform**: Android 7.0+ (SDK 24-36)
**Project Type**: Mobile (single Android app)
**Performance Goals**: 60 fps UI rendering, <100ms database operations
**Constraints**: Offline-capable (local storage only), <50MB app size impact
**Scale/Scope**: Single user, weeks of meal plans, ~100s of meals

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### I. Simplicity & YAGNI

| Rule | Status | Evidence |
|------|--------|----------|
| No features beyond requirements | ✅ PASS | Only implementing week view, meal assignment, navigation, edit/remove |
| No unnecessary abstractions | ✅ PASS | Direct Room DAO access, no repository pattern needed for local-only data |
| No speculative design | ✅ PASS | Cloud sync, custom meal slots, recipe details explicitly out of scope |

### II. Test-Driven Development

| Rule | Status | Evidence |
|------|--------|----------|
| Tests before implementation | ✅ WILL COMPLY | Tasks will be structured with test-first approach |
| Red-Green-Refactor cycle | ✅ WILL COMPLY | Each user story will have failing tests before implementation |
| Unit tests for business logic | ✅ PLANNED | ScheduledMeal date/slot logic, week navigation calculations |
| Instrumented tests for UI | ✅ PLANNED | Compose tests for week view, meal slot interactions |

### III. Compose-First UI

| Rule | Status | Evidence |
|------|--------|----------|
| Compose for all UI | ✅ PASS | WeekView, DayColumn, MealSlot all Compose composables |
| Unidirectional data flow | ✅ PASS | ViewModel holds state, UI observes and emits events |
| @Preview annotations | ✅ WILL COMPLY | All composables will have preview functions |
| Stateless composables | ✅ PASS | State hoisted to ViewModel, composables receive data and callbacks |

**Constitution Check Result**: ✅ PASSED - No violations requiring justification

## Project Structure

### Documentation (this feature)

```text
specs/001-meal-planning/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output (internal component contracts)
└── tasks.md             # Phase 2 output (/speckit.tasks command)
```

### Source Code (repository root)

```text
app/src/main/java/com/example/mealplanner/
├── data/
│   ├── local/
│   │   ├── MealPlannerDatabase.kt
│   │   ├── dao/
│   │   │   ├── MealDao.kt
│   │   │   └── ScheduledMealDao.kt
│   │   └── entity/
│   │       ├── MealEntity.kt
│   │       └── ScheduledMealEntity.kt
│   └── model/
│       ├── Meal.kt
│       ├── ScheduledMeal.kt
│       └── MealSlotType.kt
├── mealplan/
│   ├── MealPlanViewModel.kt
│   └── ui/
│       ├── MealPlanScreen.kt
│       ├── WeekView.kt
│       ├── DayColumn.kt
│       ├── MealSlotCard.kt
│       └── MealPicker.kt
└── ui/
    ├── theme/
    │   └── [existing theme files]
    └── components/
        └── EmptyState.kt

app/src/test/java/com/example/mealplanner/
├── mealplan/
│   └── MealPlanViewModelTest.kt
└── data/
    └── WeekCalculatorTest.kt

app/src/androidTest/java/com/example/mealplanner/
└── mealplan/
    ├── MealPlanScreenTest.kt
    └── WeekNavigationTest.kt
```

**Structure Decision**: Mobile app structure with feature-based organization. The `mealplan/` package contains all feature-specific code. Shared data models in `data/model/`, database layer in `data/local/`. This follows the constitution's code organization guidelines.

## Complexity Tracking

> No violations to justify. Design follows simplest path:
> - Room for persistence (AndroidX standard, no external ORM)
> - ViewModel + StateFlow for state management (no Redux/MVI complexity)
> - Direct DAO access (no repository abstraction for local-only data)
