package com.example.mealplanner.data.model

import java.time.LocalDate

data class ScheduledMeal(
    val id: Long,
    val meal: Meal,
    val date: LocalDate,
    val slotType: MealSlotType
)
