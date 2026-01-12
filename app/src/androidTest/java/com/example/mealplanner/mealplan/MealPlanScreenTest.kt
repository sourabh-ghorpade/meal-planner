package com.example.mealplanner.mealplan

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import com.example.mealplanner.data.model.Meal
import com.example.mealplanner.data.model.MealSlotType
import com.example.mealplanner.mealplan.ui.DayColumn
import com.example.mealplanner.mealplan.ui.MealSlotCard
import com.example.mealplanner.ui.theme.MealPlannerTheme
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class MealPlanScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ============================================
    // MealSlotCard Tests
    // ============================================

    @Test
    fun mealSlotCard_emptySlot_showsAddIcon() {
        composeTestRule.setContent {
            MealPlannerTheme {
                MealSlotCard(
                    slotType = MealSlotType.BREAKFAST,
                    meal = null,
                    onClick = {},
                    onRemove = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Add Breakfast").assertIsDisplayed()
    }

    @Test
    fun mealSlotCard_filledSlot_showsMealName() {
        val meal = Meal(1L, "Oatmeal")

        composeTestRule.setContent {
            MealPlannerTheme {
                MealSlotCard(
                    slotType = MealSlotType.BREAKFAST,
                    meal = meal,
                    onClick = {},
                    onRemove = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Oatmeal").assertIsDisplayed()
    }

    @Test
    fun mealSlotCard_filledSlot_showsSlotLabel() {
        val meal = Meal(1L, "Oatmeal")

        composeTestRule.setContent {
            MealPlannerTheme {
                MealSlotCard(
                    slotType = MealSlotType.BREAKFAST,
                    meal = meal,
                    onClick = {},
                    onRemove = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Breakfast").assertIsDisplayed()
    }

    @Test
    fun mealSlotCard_onClick_callsCallback() {
        var clicked = false

        composeTestRule.setContent {
            MealPlannerTheme {
                MealSlotCard(
                    slotType = MealSlotType.LUNCH,
                    meal = null,
                    onClick = { clicked = true },
                    onRemove = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Lunch: Empty. Tap to add meal.").performClick()

        assert(clicked) { "Expected onClick to be called" }
    }

    @Test
    fun mealSlotCard_onLongClick_callsOnRemove() {
        val meal = Meal(1L, "Pasta")
        var removed = false

        composeTestRule.setContent {
            MealPlannerTheme {
                MealSlotCard(
                    slotType = MealSlotType.DINNER,
                    meal = meal,
                    onClick = {},
                    onRemove = { removed = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Dinner: Pasta. Long press to remove.")
            .performTouchInput { longClick() }

        assert(removed) { "Expected onRemove to be called on long press" }
    }

    @Test
    fun mealSlotCard_emptySlot_longClickDoesNotCallOnRemove() {
        var removed = false

        composeTestRule.setContent {
            MealPlannerTheme {
                MealSlotCard(
                    slotType = MealSlotType.BREAKFAST,
                    meal = null,
                    onClick = {},
                    onRemove = { removed = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Breakfast: Empty. Tap to add meal.")
            .performTouchInput { longClick() }

        assert(!removed) { "Expected onRemove NOT to be called on empty slot" }
    }

    // ============================================
    // DayColumn Tests
    // ============================================

    @Test
    fun dayColumn_displaysAllMealSlots() {
        val day = DayWithMeals(
            date = LocalDate.of(2026, 1, 12),
            isPast = false,
            isToday = false,
            slots = MealSlotType.entries.associateWith { null }
        )

        composeTestRule.setContent {
            MealPlannerTheme {
                DayColumn(
                    day = day,
                    onSlotClick = {},
                    onMealRemove = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Breakfast").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lunch").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dinner").assertIsDisplayed()
    }

    @Test
    fun dayColumn_displaysDayLabel() {
        val day = DayWithMeals(
            date = LocalDate.of(2026, 1, 12), // Monday
            isPast = false,
            isToday = false,
            slots = MealSlotType.entries.associateWith { null }
        )

        composeTestRule.setContent {
            MealPlannerTheme {
                DayColumn(
                    day = day,
                    onSlotClick = {},
                    onMealRemove = {}
                )
            }
        }

        composeTestRule.onNodeWithText("MON").assertIsDisplayed()
        composeTestRule.onNodeWithText("12").assertIsDisplayed()
    }

    @Test
    fun dayColumn_slotClick_callsOnSlotClickWithCorrectType() {
        val day = DayWithMeals(
            date = LocalDate.of(2026, 1, 12),
            isPast = false,
            isToday = false,
            slots = MealSlotType.entries.associateWith { null }
        )
        var clickedSlot: MealSlotType? = null

        composeTestRule.setContent {
            MealPlannerTheme {
                DayColumn(
                    day = day,
                    onSlotClick = { clickedSlot = it },
                    onMealRemove = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Lunch: Empty. Tap to add meal.").performClick()

        assert(clickedSlot == MealSlotType.LUNCH) { "Expected LUNCH slot click, got $clickedSlot" }
    }

    @Test
    fun dayColumn_longPress_callsOnMealRemove() {
        val meal = Meal(1L, "Salad")
        val day = DayWithMeals(
            date = LocalDate.of(2026, 1, 12),
            isPast = false,
            isToday = false,
            slots = mapOf(
                MealSlotType.BREAKFAST to null,
                MealSlotType.LUNCH to meal,
                MealSlotType.DINNER to null
            )
        )
        var removedSlot: MealSlotType? = null

        composeTestRule.setContent {
            MealPlannerTheme {
                DayColumn(
                    day = day,
                    onSlotClick = {},
                    onMealRemove = { removedSlot = it }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Lunch: Salad. Long press to remove.")
            .performTouchInput { longClick() }

        assert(removedSlot == MealSlotType.LUNCH) { "Expected LUNCH slot remove, got $removedSlot" }
    }

    @Test
    fun dayColumn_withFilledSlots_displaysMealNames() {
        val day = DayWithMeals(
            date = LocalDate.of(2026, 1, 12),
            isPast = false,
            isToday = false,
            slots = mapOf(
                MealSlotType.BREAKFAST to Meal(1L, "Eggs"),
                MealSlotType.LUNCH to Meal(2L, "Sandwich"),
                MealSlotType.DINNER to Meal(3L, "Steak")
            )
        )

        composeTestRule.setContent {
            MealPlannerTheme {
                DayColumn(
                    day = day,
                    onSlotClick = {},
                    onMealRemove = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Eggs").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sandwich").assertIsDisplayed()
        composeTestRule.onNodeWithText("Steak").assertIsDisplayed()
    }
}
