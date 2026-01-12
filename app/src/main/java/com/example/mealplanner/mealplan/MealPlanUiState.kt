package com.example.mealplanner.mealplan

import com.example.mealplanner.data.model.Meal
import com.example.mealplanner.data.model.MealSlotType
import java.time.LocalDate

data class MealPlanUiState(
    val currentWeekStart: LocalDate,
    val weekDays: List<DayWithMeals>,
    val availableMeals: List<Meal>,
    val isLoading: Boolean,
    val selectedSlot: SelectedSlot?,
    val mealToRemove: MealRemovalRequest?,
    val error: String?
)

data class MealRemovalRequest(
    val date: LocalDate,
    val slotType: MealSlotType,
    val mealName: String
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
