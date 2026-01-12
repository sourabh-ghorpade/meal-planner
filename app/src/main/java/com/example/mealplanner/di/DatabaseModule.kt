package com.example.mealplanner.di

import android.content.Context
import androidx.room.Room
import com.example.mealplanner.data.local.MealPlannerDatabase
import com.example.mealplanner.data.local.dao.MealDao
import com.example.mealplanner.data.local.dao.ScheduledMealDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMealPlannerDatabase(
        @ApplicationContext context: Context
    ): MealPlannerDatabase {
        return Room.databaseBuilder(
            context,
            MealPlannerDatabase::class.java,
            MealPlannerDatabase.DATABASE_NAME
        )
            .addCallback(MealPlannerDatabase.seedDatabaseCallback())
            .build()
    }

    @Provides
    fun provideMealDao(database: MealPlannerDatabase): MealDao {
        return database.mealDao()
    }

    @Provides
    fun provideScheduledMealDao(database: MealPlannerDatabase): ScheduledMealDao {
        return database.scheduledMealDao()
    }
}
