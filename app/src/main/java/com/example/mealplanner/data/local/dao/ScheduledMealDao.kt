package com.example.mealplanner.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduledMealDao {

    @Query("""
        SELECT sm.id, sm.date, sm.slot_type, m.id as meal_id, m.name as meal_name
        FROM scheduled_meals sm
        INNER JOIN meals m ON sm.meal_id = m.id
        WHERE sm.date >= :startDate AND sm.date <= :endDate
        ORDER BY sm.date, sm.slot_type
    """)
    fun getScheduledMealsForWeek(startDate: Long, endDate: Long): Flow<List<ScheduledMealWithMeal>>

    @Query("""
        INSERT OR REPLACE INTO scheduled_meals (meal_id, date, slot_type)
        VALUES (:mealId, :date, :slotType)
    """)
    suspend fun upsertScheduledMeal(mealId: Long, date: Long, slotType: String)

    @Query("DELETE FROM scheduled_meals WHERE date = :date AND slot_type = :slotType")
    suspend fun deleteScheduledMeal(date: Long, slotType: String)

    @Query("DELETE FROM scheduled_meals")
    suspend fun deleteAllScheduledMeals()
}
