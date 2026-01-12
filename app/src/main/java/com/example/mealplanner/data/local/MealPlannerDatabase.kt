package com.example.mealplanner.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mealplanner.data.local.dao.MealDao
import com.example.mealplanner.data.local.dao.ScheduledMealDao
import com.example.mealplanner.data.local.entity.MealEntity
import com.example.mealplanner.data.local.entity.ScheduledMealEntity

@Database(
    entities = [MealEntity::class, ScheduledMealEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MealPlannerDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun scheduledMealDao(): ScheduledMealDao

    companion object {
        const val DATABASE_NAME = "meal_planner_database"

        fun seedDatabaseCallback(): Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Insert sample meals for testing
                db.execSQL("INSERT INTO meals (name) VALUES ('Oatmeal with Berries')")
                db.execSQL("INSERT INTO meals (name) VALUES ('Scrambled Eggs')")
                db.execSQL("INSERT INTO meals (name) VALUES ('Avocado Toast')")
                db.execSQL("INSERT INTO meals (name) VALUES ('Grilled Chicken Salad')")
                db.execSQL("INSERT INTO meals (name) VALUES ('Turkey Sandwich')")
                db.execSQL("INSERT INTO meals (name) VALUES ('Pasta Primavera')")
                db.execSQL("INSERT INTO meals (name) VALUES ('Salmon with Vegetables')")
                db.execSQL("INSERT INTO meals (name) VALUES ('Stir Fry')")
            }
        }
    }
}
