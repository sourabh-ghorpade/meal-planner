package com.example.mealplanner.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mealplanner.data.model.Meal

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String
) {
    fun toDomain(): Meal = Meal(
        id = id,
        name = name
    )

    companion object {
        fun fromDomain(meal: Meal): MealEntity = MealEntity(
            id = meal.id,
            name = meal.name
        )
    }
}
