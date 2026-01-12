# Tasks: Meal Planning

**Input**: Design documents from `/specs/001-meal-planning/`
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, contracts/ ✅

**Tests**: Following TDD per constitution - tests are written FIRST and must FAIL before implementation.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

Based on plan.md structure:
- **Source**: `app/src/main/java/com/example/mealplanner/`
- **Unit Tests**: `app/src/test/java/com/example/mealplanner/`
- **Android Tests**: `app/src/androidTest/java/com/example/mealplanner/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and dependency configuration

- [x] T001 Add Room, Lifecycle, and Compose dependencies to `app/build.gradle.kts`
- [x] T002 [P] Create package structure: `data/local/dao/`, `data/local/entity/`, `data/model/`, `mealplan/ui/`
- [x] T003 [P] Configure Room schema export in build.gradle.kts

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core data layer that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

### Tests for Foundation

- [x] T004 [P] Write unit test for MealSlotType enum in `app/src/test/java/com/example/mealplanner/data/model/MealSlotTypeTest.kt`
- [x] T005 [P] Write unit test for Meal domain model in `app/src/test/java/com/example/mealplanner/data/model/MealTest.kt`

### Implementation for Foundation

- [x] T006 [P] Create MealSlotType enum in `app/src/main/java/com/example/mealplanner/data/model/MealSlotType.kt`
- [x] T007 [P] Create Meal domain model in `app/src/main/java/com/example/mealplanner/data/model/Meal.kt`
- [x] T008 [P] Create ScheduledMeal domain model in `app/src/main/java/com/example/mealplanner/data/model/ScheduledMeal.kt`
- [x] T009 [P] Create MealEntity Room entity in `app/src/main/java/com/example/mealplanner/data/local/entity/MealEntity.kt`
- [x] T010 [P] Create ScheduledMealEntity Room entity in `app/src/main/java/com/example/mealplanner/data/local/entity/ScheduledMealEntity.kt`
- [x] T011 Create Converters class for Room type converters in `app/src/main/java/com/example/mealplanner/data/local/Converters.kt`
- [x] T012 Create MealDao interface per dao-contract.md in `app/src/main/java/com/example/mealplanner/data/local/dao/MealDao.kt`
- [x] T013 Create ScheduledMealDao interface per dao-contract.md in `app/src/main/java/com/example/mealplanner/data/local/dao/ScheduledMealDao.kt`
- [x] T014 Create ScheduledMealWithMeal DTO in `app/src/main/java/com/example/mealplanner/data/local/dao/ScheduledMealWithMeal.kt`
- [x] T015 Create MealPlannerDatabase with DAOs and seed data in `app/src/main/java/com/example/mealplanner/data/local/MealPlannerDatabase.kt`
- [x] T016 Create Hilt module for database dependency injection in `app/src/main/java/com/example/mealplanner/di/DatabaseModule.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Create Weekly Meal Plan (Priority: P1) 🎯 MVP

**Goal**: Users can view a 7-day week and assign meals to breakfast/lunch/dinner slots

**Independent Test**: Create a meal plan, assign at least one meal to each day slot, verify plan persists and displays correctly

### Tests for User Story 1

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [x] T017 [P] [US1] Write unit test for week calculation logic in `app/src/test/java/com/example/mealplanner/data/WeekCalculatorTest.kt`
- [x] T018 [P] [US1] Write unit test for MealPlanViewModel initial state in `app/src/test/java/com/example/mealplanner/mealplan/MealPlanViewModelTest.kt`
- [x] T019 [P] [US1] Write unit test for MealPlanViewModel.selectSlot() in `app/src/test/java/com/example/mealplanner/mealplan/MealPlanViewModelTest.kt`
- [x] T020 [P] [US1] Write unit test for MealPlanViewModel.assignMeal() in `app/src/test/java/com/example/mealplanner/mealplan/MealPlanViewModelTest.kt`

### Implementation for User Story 1

- [x] T021 [US1] Create DayWithMeals and SelectedSlot UI state models in `app/src/main/java/com/example/mealplanner/mealplan/MealPlanUiState.kt`
- [x] T022 [US1] Create WeekCalculator utility for week start/end calculations in `app/src/main/java/com/example/mealplanner/data/WeekCalculator.kt`
- [x] T023 [US1] Create MealPlanViewModel with initial state per viewmodel-contract.md in `app/src/main/java/com/example/mealplanner/mealplan/MealPlanViewModel.kt`
- [x] T024 [US1] Implement selectSlot() and dismissPicker() actions in MealPlanViewModel
- [x] T025 [US1] Implement assignMeal() action with database persistence in MealPlanViewModel
- [x] T026 [P] [US1] Create EmptyState reusable composable in `app/src/main/java/com/example/mealplanner/ui/components/EmptyState.kt`
- [x] T027 [P] [US1] Create MealSlotCard composable per ui-components-contract.md in `app/src/main/java/com/example/mealplanner/mealplan/ui/MealSlotCard.kt`
- [x] T028 [US1] Create DayColumn composable per ui-components-contract.md in `app/src/main/java/com/example/mealplanner/mealplan/ui/DayColumn.kt`
- [x] T029 [US1] Create MealPickerBottomSheet composable per ui-components-contract.md in `app/src/main/java/com/example/mealplanner/mealplan/ui/MealPickerBottomSheet.kt`
- [x] T030 [US1] Create WeekView composable (without navigation) per ui-components-contract.md in `app/src/main/java/com/example/mealplanner/mealplan/ui/WeekView.kt`
- [x] T031 [US1] Create MealPlanScreen composable per ui-components-contract.md in `app/src/main/java/com/example/mealplanner/mealplan/ui/MealPlanScreen.kt`
- [x] T032 [US1] Add MealPlanScreen to app navigation graph in `app/src/main/java/com/example/mealplanner/MainActivity.kt`

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - Navigate Between Weeks (Priority: P2)

**Goal**: Users can navigate to past/future weeks and quickly return to the current week

**Independent Test**: Navigate to next week, assign a meal, return to current week, verify both weeks retain independent assignments

### Tests for User Story 2

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [x] T033 [P] [US2] Write unit test for MealPlanViewModel.navigateToWeek() in `app/src/test/java/com/example/mealplanner/mealplan/MealPlanViewModelTest.kt`
- [x] T034 [P] [US2] Write unit test for MealPlanViewModel.goToCurrentWeek() in `app/src/test/java/com/example/mealplanner/mealplan/MealPlanViewModelTest.kt`
- [x] T035 [P] [US2] Write instrumented test for week navigation in `app/src/androidTest/java/com/example/mealplanner/mealplan/WeekNavigationTest.kt`

### Implementation for User Story 2

- [x] T036 [US2] Implement navigateToWeek() action in MealPlanViewModel
- [x] T037 [US2] Implement goToCurrentWeek() action in MealPlanViewModel
- [x] T038 [US2] Add week navigation header with prev/next arrows to WeekView composable
- [x] T039 [US2] Add "Today" button to WeekView header for quick return to current week
- [x] T040 [US2] Add visual highlight for isToday in DayColumn composable

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - Edit and Remove Scheduled Meals (Priority: P3)

**Goal**: Users can replace or remove meals from scheduled slots

**Independent Test**: Schedule a meal, edit it to a different meal, then remove it entirely, verify each change persists

### Tests for User Story 3

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [x] T041 [P] [US3] Write unit test for MealPlanViewModel.removeMeal() in `app/src/test/java/com/example/mealplanner/mealplan/MealPlanViewModelTest.kt`
- [x] T042 [P] [US3] Write instrumented test for meal editing flow in `app/src/androidTest/java/com/example/mealplanner/mealplan/MealPlanScreenTest.kt`

### Implementation for User Story 3

- [x] T043 [US3] Implement removeMeal() action with database delete in MealPlanViewModel
- [x] T044 [US3] Add long-press gesture handler to MealSlotCard for remove action
- [x] T045 [US3] Add confirmation dialog for meal removal
- [x] T046 [US3] Update MealSlotCard to show different visual state for filled vs empty slots

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [x] T047 [P] Add loading indicator to MealPlanScreen when isLoading is true
- [x] T048 [P] Add error snackbar display when error is non-null
- [x] T049 [P] Implement clearError() action in MealPlanViewModel
- [x] T050 [P] Add @Preview annotations to all composables per constitution
- [x] T051 [P] Add contentDescription for accessibility to all interactive elements
- [x] T052 [P] Ensure 48dp minimum touch targets per accessibility requirements
- [x] T053 [P] Add isPast visual styling to DayColumn for past dates
- [x] T054 Run quickstart.md validation tests (manual verification)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-5)**: All depend on Foundational phase completion
  - User stories can proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Phase 6)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Builds on US1 WeekView but independently testable
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Builds on US1 MealSlotCard but independently testable

### Within Each User Story (TDD)

1. Write tests FIRST → Verify they FAIL
2. Implement models/utilities
3. Implement ViewModel logic
4. Implement UI components
5. Verify tests PASS
6. Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundation models (T006-T010) can be created in parallel
- All tests for a user story marked [P] can run in parallel
- UI components marked [P] can be built in parallel
- Different user stories can be worked on in parallel by different team members

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Add User Story 3 → Test independently → Deploy/Demo
5. Each story adds value without breaking previous stories

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing (TDD per constitution)
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
