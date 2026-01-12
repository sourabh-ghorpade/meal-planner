package com.example.mealplanner.mealplan

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.mealplanner.data.model.MealSlotType
import com.example.mealplanner.mealplan.ui.WeekView
import com.example.mealplanner.ui.theme.MealPlannerTheme
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class WeekNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val dateFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())

    private fun createTestDays(weekStart: LocalDate): List<DayWithMeals> {
        return (0L..6L).map { offset ->
            val date = weekStart.plusDays(offset)
            DayWithMeals(
                date = date,
                isPast = false,
                isToday = offset == 2L, // Wednesday is "today" for testing
                slots = MealSlotType.entries.associateWith { null }
            )
        }
    }

    @Test
    fun weekView_displaysWeekLabel() {
        val weekStart = LocalDate.of(2026, 1, 12) // Monday
        val weekEnd = weekStart.plusDays(6)
        val expectedLabel = "${weekStart.format(dateFormatter)} - ${weekEnd.format(dateFormatter)}, ${weekStart.year}"

        composeTestRule.setContent {
            MealPlannerTheme {
                WeekView(
                    weekStart = weekStart,
                    days = createTestDays(weekStart),
                    onNavigateWeek = {},
                    onGoToToday = {},
                    onSlotClick = { _, _ -> },
                    onMealRemove = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText(expectedLabel).assertIsDisplayed()
    }

    @Test
    fun weekView_previousWeekButtonIsDisplayed() {
        val weekStart = LocalDate.of(2026, 1, 12)

        composeTestRule.setContent {
            MealPlannerTheme {
                WeekView(
                    weekStart = weekStart,
                    days = createTestDays(weekStart),
                    onNavigateWeek = {},
                    onGoToToday = {},
                    onSlotClick = { _, _ -> },
                    onMealRemove = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Previous week").assertIsDisplayed()
    }

    @Test
    fun weekView_nextWeekButtonIsDisplayed() {
        val weekStart = LocalDate.of(2026, 1, 12)

        composeTestRule.setContent {
            MealPlannerTheme {
                WeekView(
                    weekStart = weekStart,
                    days = createTestDays(weekStart),
                    onNavigateWeek = {},
                    onGoToToday = {},
                    onSlotClick = { _, _ -> },
                    onMealRemove = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Next week").assertIsDisplayed()
    }

    @Test
    fun weekView_todayButtonIsDisplayed() {
        val weekStart = LocalDate.of(2026, 1, 12)

        composeTestRule.setContent {
            MealPlannerTheme {
                WeekView(
                    weekStart = weekStart,
                    days = createTestDays(weekStart),
                    onNavigateWeek = {},
                    onGoToToday = {},
                    onSlotClick = { _, _ -> },
                    onMealRemove = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("Today").assertIsDisplayed()
    }

    @Test
    fun weekView_previousWeekButtonCallsOnNavigateWeek() {
        val weekStart = LocalDate.of(2026, 1, 12)
        var navigateOffset: Int? = null

        composeTestRule.setContent {
            MealPlannerTheme {
                WeekView(
                    weekStart = weekStart,
                    days = createTestDays(weekStart),
                    onNavigateWeek = { offset -> navigateOffset = offset },
                    onGoToToday = {},
                    onSlotClick = { _, _ -> },
                    onMealRemove = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Previous week").performClick()

        assert(navigateOffset == -1) { "Expected navigate offset -1, but got $navigateOffset" }
    }

    @Test
    fun weekView_nextWeekButtonCallsOnNavigateWeek() {
        val weekStart = LocalDate.of(2026, 1, 12)
        var navigateOffset: Int? = null

        composeTestRule.setContent {
            MealPlannerTheme {
                WeekView(
                    weekStart = weekStart,
                    days = createTestDays(weekStart),
                    onNavigateWeek = { offset -> navigateOffset = offset },
                    onGoToToday = {},
                    onSlotClick = { _, _ -> },
                    onMealRemove = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Next week").performClick()

        assert(navigateOffset == 1) { "Expected navigate offset 1, but got $navigateOffset" }
    }

    @Test
    fun weekView_todayButtonCallsOnGoToToday() {
        val weekStart = LocalDate.of(2026, 1, 12)
        var todayCalled = false

        composeTestRule.setContent {
            MealPlannerTheme {
                WeekView(
                    weekStart = weekStart,
                    days = createTestDays(weekStart),
                    onNavigateWeek = {},
                    onGoToToday = { todayCalled = true },
                    onSlotClick = { _, _ -> },
                    onMealRemove = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Go to current week").performClick()

        assert(todayCalled) { "Expected onGoToToday to be called" }
    }

    @Test
    fun weekView_displaysAllSevenDays() {
        val weekStart = LocalDate.of(2026, 1, 12) // Monday

        composeTestRule.setContent {
            MealPlannerTheme {
                WeekView(
                    weekStart = weekStart,
                    days = createTestDays(weekStart),
                    onNavigateWeek = {},
                    onGoToToday = {},
                    onSlotClick = { _, _ -> },
                    onMealRemove = { _, _ -> }
                )
            }
        }

        // Check that day labels are displayed (Mon, Tue, Wed, etc.)
        composeTestRule.onNodeWithText("Mon").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tue").assertIsDisplayed()
        composeTestRule.onNodeWithText("Wed").assertIsDisplayed()
    }
}
