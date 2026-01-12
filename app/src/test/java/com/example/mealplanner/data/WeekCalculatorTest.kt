package com.example.mealplanner.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class WeekCalculatorTest {

    @Test
    fun `getWeekStart returns Monday for date on Monday`() {
        val monday = LocalDate.of(2026, 1, 12) // Monday
        val result = WeekCalculator.getWeekStart(monday)
        assertEquals(monday, result)
    }

    @Test
    fun `getWeekStart returns Monday for date on Wednesday`() {
        val wednesday = LocalDate.of(2026, 1, 14) // Wednesday
        val expectedMonday = LocalDate.of(2026, 1, 12)
        val result = WeekCalculator.getWeekStart(wednesday)
        assertEquals(expectedMonday, result)
    }

    @Test
    fun `getWeekStart returns Monday for date on Sunday`() {
        val sunday = LocalDate.of(2026, 1, 18) // Sunday
        val expectedMonday = LocalDate.of(2026, 1, 12)
        val result = WeekCalculator.getWeekStart(sunday)
        assertEquals(expectedMonday, result)
    }

    @Test
    fun `getWeekEnd returns Sunday for given Monday`() {
        val monday = LocalDate.of(2026, 1, 12)
        val expectedSunday = LocalDate.of(2026, 1, 18)
        val result = WeekCalculator.getWeekEnd(monday)
        assertEquals(expectedSunday, result)
    }

    @Test
    fun `getWeekDates returns 7 consecutive days starting from Monday`() {
        val monday = LocalDate.of(2026, 1, 12)
        val result = WeekCalculator.getWeekDates(monday)

        assertEquals(7, result.size)
        assertEquals(monday, result[0])
        assertEquals(monday.plusDays(1), result[1])
        assertEquals(monday.plusDays(6), result[6])
    }

    @Test
    fun `getWeekDates all days are in correct order`() {
        val monday = LocalDate.of(2026, 1, 12)
        val result = WeekCalculator.getWeekDates(monday)

        assertEquals(DayOfWeek.MONDAY, result[0].dayOfWeek)
        assertEquals(DayOfWeek.TUESDAY, result[1].dayOfWeek)
        assertEquals(DayOfWeek.WEDNESDAY, result[2].dayOfWeek)
        assertEquals(DayOfWeek.THURSDAY, result[3].dayOfWeek)
        assertEquals(DayOfWeek.FRIDAY, result[4].dayOfWeek)
        assertEquals(DayOfWeek.SATURDAY, result[5].dayOfWeek)
        assertEquals(DayOfWeek.SUNDAY, result[6].dayOfWeek)
    }

    @Test
    fun `navigateWeek with offset 1 returns next week start`() {
        val currentMonday = LocalDate.of(2026, 1, 12)
        val expectedNextMonday = LocalDate.of(2026, 1, 19)
        val result = WeekCalculator.navigateWeek(currentMonday, 1)
        assertEquals(expectedNextMonday, result)
    }

    @Test
    fun `navigateWeek with offset -1 returns previous week start`() {
        val currentMonday = LocalDate.of(2026, 1, 12)
        val expectedPrevMonday = LocalDate.of(2026, 1, 5)
        val result = WeekCalculator.navigateWeek(currentMonday, -1)
        assertEquals(expectedPrevMonday, result)
    }

    @Test
    fun `navigateWeek with offset 0 returns same week start`() {
        val currentMonday = LocalDate.of(2026, 1, 12)
        val result = WeekCalculator.navigateWeek(currentMonday, 0)
        assertEquals(currentMonday, result)
    }
}
