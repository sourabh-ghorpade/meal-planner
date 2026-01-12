package com.example.mealplanner.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class MealSlotTypeTest {

    @Test
    fun `MealSlotType has three values`() {
        val values = MealSlotType.values()
        assertEquals(3, values.size)
    }

    @Test
    fun `MealSlotType values are in correct order`() {
        val values = MealSlotType.values()
        assertEquals(MealSlotType.BREAKFAST, values[0])
        assertEquals(MealSlotType.LUNCH, values[1])
        assertEquals(MealSlotType.DINNER, values[2])
    }

    @Test
    fun `MealSlotType displayName returns correct values`() {
        assertEquals("Breakfast", MealSlotType.BREAKFAST.displayName)
        assertEquals("Lunch", MealSlotType.LUNCH.displayName)
        assertEquals("Dinner", MealSlotType.DINNER.displayName)
    }

    @Test
    fun `MealSlotType valueOf works correctly`() {
        assertEquals(MealSlotType.BREAKFAST, MealSlotType.valueOf("BREAKFAST"))
        assertEquals(MealSlotType.LUNCH, MealSlotType.valueOf("LUNCH"))
        assertEquals(MealSlotType.DINNER, MealSlotType.valueOf("DINNER"))
    }
}
