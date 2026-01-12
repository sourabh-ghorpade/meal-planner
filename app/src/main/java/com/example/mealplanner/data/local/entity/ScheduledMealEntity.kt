package com.example.mealplanner.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "scheduled_meals",
    foreignKeys = [
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["id"],
            childColumns = ["meal_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["date"]),
        Index(value = ["date", "slot_type"], unique = true),
        Index(value = ["meal_id"])
    ]
)
data class ScheduledMealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "meal_id")
    val mealId: Long,

    @ColumnInfo(name = "date")
    val date: Long, // Epoch day (LocalDate.toEpochDay())

    @ColumnInfo(name = "slot_type")
    val slotType: String // MealSlotType.name
)
