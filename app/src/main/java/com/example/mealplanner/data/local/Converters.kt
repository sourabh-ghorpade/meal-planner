package com.example.mealplanner.data.local

import androidx.room.TypeConverter
import com.example.mealplanner.data.model.MealSlotType
import java.time.LocalDate

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
