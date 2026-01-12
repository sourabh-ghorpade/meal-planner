package com.example.mealplanner.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class MealTest {

    @Test
    fun `Meal can be created with id and name`() {
        val meal = Meal(id = 1L, name = "Oatmeal")
        assertEquals(1L, meal.id)
        assertEquals("Oatmeal", meal.name)
    }

    @Test
    fun `Meals with same id and name are equal`() {
        val meal1 = Meal(id = 1L, name = "Oatmeal")
        val meal2 = Meal(id = 1L, name = "Oatmeal")
        assertEquals(meal1, meal2)
    }

    @Test
    fun `Meals with different ids are not equal`() {
        val meal1 = Meal(id = 1L, name = "Oatmeal")
        val meal2 = Meal(id = 2L, name = "Oatmeal")
        assertNotEquals(meal1, meal2)
    }

    @Test
    fun `Meals with different names are not equal`() {
        val meal1 = Meal(id = 1L, name = "Oatmeal")
        val meal2 = Meal(id = 1L, name = "Scrambled Eggs")
        assertNotEquals(meal1, meal2)
    }

    @Test
    fun `Meal copy works correctly`() {
        val meal = Meal(id = 1L, name = "Oatmeal")
        val copied = meal.copy(name = "Updated Oatmeal")
        assertEquals(1L, copied.id)
        assertEquals("Updated Oatmeal", copied.name)
    }
}
