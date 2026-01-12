package com.example.mealplanner.data

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

object WeekCalculator {

    fun getWeekStart(date: LocalDate): LocalDate {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }

    fun getWeekEnd(weekStart: LocalDate): LocalDate {
        return weekStart.plusDays(6)
    }

    fun getWeekDates(weekStart: LocalDate): List<LocalDate> {
        return (0L..6L).map { weekStart.plusDays(it) }
    }

    fun navigateWeek(currentWeekStart: LocalDate, offset: Int): LocalDate {
        return currentWeekStart.plusWeeks(offset.toLong())
    }
}
