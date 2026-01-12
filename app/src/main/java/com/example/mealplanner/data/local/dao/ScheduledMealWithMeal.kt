package com.example.mealplanner.data.local.dao

import androidx.room.ColumnInfo
import com.example.mealplanner.data.model.Meal
import com.example.mealplanner.data.model.MealSlotType
import com.example.mealplanner.data.model.ScheduledMeal
import java.time.LocalDate

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
) {
    fun toDomain(): ScheduledMeal = ScheduledMeal(
        id = id,
        meal = Meal(id = mealId, name = mealName),
        date = LocalDate.ofEpochDay(date),
        slotType = MealSlotType.valueOf(slotType)
    )
}
