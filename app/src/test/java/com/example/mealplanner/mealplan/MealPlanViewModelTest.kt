package com.example.mealplanner.mealplan

import com.example.mealplanner.data.WeekCalculator
import com.example.mealplanner.data.local.dao.MealDao
import com.example.mealplanner.data.local.dao.ScheduledMealDao
import com.example.mealplanner.data.local.dao.ScheduledMealWithMeal
import com.example.mealplanner.data.local.entity.MealEntity
import com.example.mealplanner.data.model.Meal
import com.example.mealplanner.data.model.MealSlotType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class MealPlanViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mealDao: MealDao
    private lateinit var scheduledMealDao: ScheduledMealDao
    private lateinit var viewModel: MealPlanViewModel

    private val today = LocalDate.of(2026, 1, 14) // Wednesday
    private val currentWeekStart = LocalDate.of(2026, 1, 12) // Monday

    private val sampleMeals = listOf(
        MealEntity(1L, "Oatmeal"),
        MealEntity(2L, "Salad"),
        MealEntity(3L, "Pasta")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mealDao = mock()
        scheduledMealDao = mock()

        whenever(mealDao.getAllMeals()).thenReturn(flowOf(sampleMeals))
        whenever(scheduledMealDao.getScheduledMealsForWeek(any(), any()))
            .thenReturn(flowOf(emptyList()))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): MealPlanViewModel {
        return MealPlanViewModel(mealDao, scheduledMealDao, today)
    }

    // ============================================
    // T018: Initial State Tests
    // ============================================

    @Test
    fun `initial state has correct week start based on today`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(currentWeekStart, viewModel.uiState.value.currentWeekStart)
    }

    @Test
    fun `initial state has isLoading true before data loads`() = runTest {
        viewModel = createViewModel()
        // Don't advance - check immediate state
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `initial state has isLoading false after data loads`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `initial state has 7 weekDays`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(7, viewModel.uiState.value.weekDays.size)
    }

    @Test
    fun `initial state weekDays start from Monday`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(currentWeekStart, viewModel.uiState.value.weekDays[0].date)
    }

    @Test
    fun `initial state weekDays end on Sunday`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(currentWeekStart.plusDays(6), viewModel.uiState.value.weekDays[6].date)
    }

    @Test
    fun `initial state has today marked correctly`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val todayDay = viewModel.uiState.value.weekDays.find { it.isToday }
        assertNotNull(todayDay)
        assertEquals(today, todayDay?.date)
    }

    @Test
    fun `initial state has past days marked correctly`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val pastDays = viewModel.uiState.value.weekDays.filter { it.isPast }
        // Monday (12) and Tuesday (13) are past, today (14) is not past
        assertEquals(2, pastDays.size)
    }

    @Test
    fun `initial state has available meals loaded`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.availableMeals.size)
        assertEquals("Oatmeal", viewModel.uiState.value.availableMeals[0].name)
    }

    @Test
    fun `initial state has selectedSlot as null`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.selectedSlot)
    }

    @Test
    fun `initial state has error as null`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `initial state each day has 3 empty slots`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.uiState.value.weekDays.forEach { day ->
            assertEquals(3, day.slots.size)
            assertTrue(day.slots.containsKey(MealSlotType.BREAKFAST))
            assertTrue(day.slots.containsKey(MealSlotType.LUNCH))
            assertTrue(day.slots.containsKey(MealSlotType.DINNER))
            day.slots.values.forEach { meal ->
                assertNull(meal)
            }
        }
    }

    // ============================================
    // T019: SelectSlot Tests
    // ============================================

    @Test
    fun `selectSlot sets selectedSlot with correct date and slotType`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val date = currentWeekStart.plusDays(2)
        val slotType = MealSlotType.LUNCH

        viewModel.selectSlot(date, slotType)
        advanceUntilIdle()

        val selectedSlot = viewModel.uiState.value.selectedSlot
        assertNotNull(selectedSlot)
        assertEquals(date, selectedSlot?.date)
        assertEquals(slotType, selectedSlot?.slotType)
    }

    @Test
    fun `selectSlot can select breakfast slot`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.selectSlot(currentWeekStart, MealSlotType.BREAKFAST)
        advanceUntilIdle()

        assertEquals(MealSlotType.BREAKFAST, viewModel.uiState.value.selectedSlot?.slotType)
    }

    @Test
    fun `selectSlot can select dinner slot`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.selectSlot(currentWeekStart, MealSlotType.DINNER)
        advanceUntilIdle()

        assertEquals(MealSlotType.DINNER, viewModel.uiState.value.selectedSlot?.slotType)
    }

    @Test
    fun `dismissPicker clears selectedSlot`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.selectSlot(currentWeekStart, MealSlotType.LUNCH)
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.selectedSlot)

        viewModel.dismissPicker()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.selectedSlot)
    }

    // ============================================
    // T020: AssignMeal Tests
    // ============================================

    @Test
    fun `assignMeal updates the slot with the selected meal`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val date = currentWeekStart
        val slotType = MealSlotType.BREAKFAST
        val meal = Meal(1L, "Oatmeal")

        viewModel.selectSlot(date, slotType)
        advanceUntilIdle()

        viewModel.assignMeal(meal)
        advanceUntilIdle()

        val dayWithMeals = viewModel.uiState.value.weekDays.find { it.date == date }
        assertNotNull(dayWithMeals)
        assertEquals(meal, dayWithMeals?.slots?.get(slotType))
    }

    @Test
    fun `assignMeal clears selectedSlot after assignment`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.selectSlot(currentWeekStart, MealSlotType.BREAKFAST)
        advanceUntilIdle()

        viewModel.assignMeal(Meal(1L, "Oatmeal"))
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.selectedSlot)
    }

    @Test
    fun `assignMeal calls upsertScheduledMeal on DAO`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val date = currentWeekStart
        val slotType = MealSlotType.LUNCH
        val meal = Meal(2L, "Salad")

        viewModel.selectSlot(date, slotType)
        advanceUntilIdle()

        viewModel.assignMeal(meal)
        advanceUntilIdle()

        verify(scheduledMealDao).upsertScheduledMeal(
            mealId = 2L,
            date = date.toEpochDay(),
            slotType = slotType.name
        )
    }

    @Test
    fun `assignMeal does nothing when selectedSlot is null`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.selectedSlot)

        val initialState = viewModel.uiState.value
        viewModel.assignMeal(Meal(1L, "Oatmeal"))
        advanceUntilIdle()

        // State should be unchanged
        assertEquals(initialState.weekDays, viewModel.uiState.value.weekDays)
    }

    @Test
    fun `assignMeal can replace existing meal in slot`() = runTest {
        // Setup: Create a scheduled meal in the database result
        val scheduledMealWithMeal = ScheduledMealWithMeal(
            id = 1L,
            date = currentWeekStart.toEpochDay(),
            slotType = MealSlotType.BREAKFAST.name,
            mealId = 1L,
            mealName = "Oatmeal"
        )
        whenever(scheduledMealDao.getScheduledMealsForWeek(any(), any()))
            .thenReturn(flowOf(listOf(scheduledMealWithMeal)))

        viewModel = createViewModel()
        advanceUntilIdle()

        // Verify initial meal is present
        val initialMeal = viewModel.uiState.value.weekDays[0].slots[MealSlotType.BREAKFAST]
        assertEquals("Oatmeal", initialMeal?.name)

        // Replace with a different meal
        viewModel.selectSlot(currentWeekStart, MealSlotType.BREAKFAST)
        advanceUntilIdle()

        viewModel.assignMeal(Meal(3L, "Pasta"))
        advanceUntilIdle()

        val updatedMeal = viewModel.uiState.value.weekDays[0].slots[MealSlotType.BREAKFAST]
        assertEquals("Pasta", updatedMeal?.name)
    }

    // ============================================
    // T033: NavigateToWeek Tests
    // ============================================

    @Test
    fun `navigateToWeek with offset 1 moves to next week`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val initialWeekStart = viewModel.uiState.value.currentWeekStart

        viewModel.navigateToWeek(1)
        advanceUntilIdle()

        val newWeekStart = viewModel.uiState.value.currentWeekStart
        assertEquals(initialWeekStart.plusWeeks(1), newWeekStart)
    }

    @Test
    fun `navigateToWeek with offset -1 moves to previous week`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val initialWeekStart = viewModel.uiState.value.currentWeekStart

        viewModel.navigateToWeek(-1)
        advanceUntilIdle()

        val newWeekStart = viewModel.uiState.value.currentWeekStart
        assertEquals(initialWeekStart.minusWeeks(1), newWeekStart)
    }

    @Test
    fun `navigateToWeek updates weekDays to new week`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.navigateToWeek(1)
        advanceUntilIdle()

        val newWeekStart = viewModel.uiState.value.currentWeekStart
        assertEquals(newWeekStart, viewModel.uiState.value.weekDays[0].date)
        assertEquals(newWeekStart.plusDays(6), viewModel.uiState.value.weekDays[6].date)
    }

    @Test
    fun `navigateToWeek sets isLoading true while loading`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.navigateToWeek(1)
        // Before idle, should be loading
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `navigateToWeek multiple times accumulates offsets`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val initialWeekStart = viewModel.uiState.value.currentWeekStart

        viewModel.navigateToWeek(1)
        advanceUntilIdle()
        viewModel.navigateToWeek(1)
        advanceUntilIdle()

        assertEquals(initialWeekStart.plusWeeks(2), viewModel.uiState.value.currentWeekStart)
    }

    @Test
    fun `navigateToWeek to future week has no past days`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        // Navigate to a week in the future
        viewModel.navigateToWeek(2)
        advanceUntilIdle()

        // In a future week, no days should be past (relative to today)
        val pastDays = viewModel.uiState.value.weekDays.filter { it.isPast }
        assertEquals(0, pastDays.size)
    }

    @Test
    fun `navigateToWeek to past week has all days as past`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        // Navigate to a week in the past
        viewModel.navigateToWeek(-2)
        advanceUntilIdle()

        // In a past week, all days should be past
        val pastDays = viewModel.uiState.value.weekDays.filter { it.isPast }
        assertEquals(7, pastDays.size)
    }

    // ============================================
    // T034: GoToCurrentWeek Tests
    // ============================================

    @Test
    fun `goToCurrentWeek returns to week containing today`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        // Navigate away
        viewModel.navigateToWeek(3)
        advanceUntilIdle()

        // Return to current week
        viewModel.goToCurrentWeek()
        advanceUntilIdle()

        assertEquals(currentWeekStart, viewModel.uiState.value.currentWeekStart)
    }

    @Test
    fun `goToCurrentWeek does nothing when already on current week`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val initialState = viewModel.uiState.value

        viewModel.goToCurrentWeek()
        advanceUntilIdle()

        assertEquals(initialState.currentWeekStart, viewModel.uiState.value.currentWeekStart)
    }

    @Test
    fun `goToCurrentWeek from past week returns to current week`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.navigateToWeek(-5)
        advanceUntilIdle()

        viewModel.goToCurrentWeek()
        advanceUntilIdle()

        assertEquals(currentWeekStart, viewModel.uiState.value.currentWeekStart)
        val todayDay = viewModel.uiState.value.weekDays.find { it.isToday }
        assertNotNull(todayDay)
    }

    @Test
    fun `goToCurrentWeek sets isLoading true when navigating`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.navigateToWeek(2)
        advanceUntilIdle()

        viewModel.goToCurrentWeek()
        // Before idle, should be loading
        assertTrue(viewModel.uiState.value.isLoading)
    }

    // ============================================
    // T041: RemoveMeal Tests
    // ============================================

    @Test
    fun `removeMeal clears the meal from the slot`() = runTest {
        // Setup: Create a scheduled meal in the database result
        val scheduledMealWithMeal = ScheduledMealWithMeal(
            id = 1L,
            date = currentWeekStart.toEpochDay(),
            slotType = MealSlotType.BREAKFAST.name,
            mealId = 1L,
            mealName = "Oatmeal"
        )
        whenever(scheduledMealDao.getScheduledMealsForWeek(any(), any()))
            .thenReturn(flowOf(listOf(scheduledMealWithMeal)))

        viewModel = createViewModel()
        advanceUntilIdle()

        // Verify initial meal is present
        val initialMeal = viewModel.uiState.value.weekDays[0].slots[MealSlotType.BREAKFAST]
        assertNotNull(initialMeal)

        // Remove the meal
        viewModel.removeMeal(currentWeekStart, MealSlotType.BREAKFAST)
        advanceUntilIdle()

        val updatedMeal = viewModel.uiState.value.weekDays[0].slots[MealSlotType.BREAKFAST]
        assertNull(updatedMeal)
    }

    @Test
    fun `removeMeal calls deleteScheduledMeal on DAO`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val date = currentWeekStart
        val slotType = MealSlotType.LUNCH

        viewModel.removeMeal(date, slotType)
        advanceUntilIdle()

        verify(scheduledMealDao).deleteScheduledMeal(
            date = date.toEpochDay(),
            slotType = slotType.name
        )
    }

    @Test
    fun `removeMeal on empty slot does not fail`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        // All slots are empty initially
        val date = currentWeekStart
        val slotType = MealSlotType.DINNER

        // Should not throw
        viewModel.removeMeal(date, slotType)
        advanceUntilIdle()

        // Slot should still be null
        val slot = viewModel.uiState.value.weekDays[0].slots[slotType]
        assertNull(slot)
    }
}
