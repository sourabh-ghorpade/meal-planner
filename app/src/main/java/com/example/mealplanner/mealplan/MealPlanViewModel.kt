package com.example.mealplanner.mealplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.data.WeekCalculator
import com.example.mealplanner.data.local.dao.MealDao
import com.example.mealplanner.data.local.dao.ScheduledMealDao
import com.example.mealplanner.data.local.dao.ScheduledMealWithMeal
import com.example.mealplanner.data.model.Meal
import com.example.mealplanner.data.model.MealSlotType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MealPlanViewModel @Inject constructor(
    private val mealDao: MealDao,
    private val scheduledMealDao: ScheduledMealDao
) : ViewModel() {

    private var today: LocalDate = LocalDate.now()

    private val _currentWeekStart = MutableStateFlow(WeekCalculator.getWeekStart(today))

    private val _uiState = MutableStateFlow(
        MealPlanUiState(
            currentWeekStart = _currentWeekStart.value,
            weekDays = emptyList(),
            availableMeals = emptyList(),
            isLoading = true,
            selectedSlot = null,
            mealToRemove = null,
            error = null
        )
    )
    val uiState: StateFlow<MealPlanUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    // Constructor for testing with injectable today
    internal constructor(
        mealDao: MealDao,
        scheduledMealDao: ScheduledMealDao,
        testToday: LocalDate
    ) : this(mealDao, scheduledMealDao) {
        today = testToday
        val weekStart = WeekCalculator.getWeekStart(testToday)
        _currentWeekStart.value = weekStart
        _uiState.value = _uiState.value.copy(currentWeekStart = weekStart)
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                mealDao.getAllMeals(),
                _currentWeekStart.flatMapLatest { weekStart ->
                    val weekEnd = WeekCalculator.getWeekEnd(weekStart)
                    scheduledMealDao.getScheduledMealsForWeek(
                        weekStart.toEpochDay(),
                        weekEnd.toEpochDay()
                    )
                }
            ) { mealEntities, scheduledMeals ->
                val availableMeals = mealEntities.map { it.toDomain() }
                val weekDays = buildWeekDays(scheduledMeals)
                Pair(availableMeals, weekDays)
            }.collect { (availableMeals, weekDays) ->
                _uiState.update {
                    it.copy(
                        availableMeals = availableMeals,
                        weekDays = weekDays,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun buildWeekDays(scheduledMeals: List<ScheduledMealWithMeal>): List<DayWithMeals> {
        val weekStart = _currentWeekStart.value
        val scheduledMealsMap = scheduledMeals.groupBy { it.date }

        return WeekCalculator.getWeekDates(weekStart).map { date ->
            val mealsForDay = scheduledMealsMap[date.toEpochDay()] ?: emptyList()
            val slots = MealSlotType.entries.associateWith { slotType ->
                mealsForDay.find { it.slotType == slotType.name }?.let {
                    Meal(it.mealId, it.mealName)
                }
            }
            DayWithMeals(
                date = date,
                isPast = date.isBefore(today),
                isToday = date == today,
                slots = slots
            )
        }
    }

    fun selectSlot(date: LocalDate, slotType: MealSlotType) {
        _uiState.update {
            it.copy(selectedSlot = SelectedSlot(date, slotType))
        }
    }

    fun dismissPicker() {
        _uiState.update {
            it.copy(selectedSlot = null)
        }
    }

    fun assignMeal(meal: Meal) {
        val selectedSlot = _uiState.value.selectedSlot ?: return

        viewModelScope.launch {
            scheduledMealDao.upsertScheduledMeal(
                mealId = meal.id,
                date = selectedSlot.date.toEpochDay(),
                slotType = selectedSlot.slotType.name
            )

            // Update local state immediately for better UX
            _uiState.update { state ->
                val updatedWeekDays = state.weekDays.map { day ->
                    if (day.date == selectedSlot.date) {
                        day.copy(slots = day.slots + (selectedSlot.slotType to meal))
                    } else {
                        day
                    }
                }
                state.copy(
                    weekDays = updatedWeekDays,
                    selectedSlot = null
                )
            }
        }
    }

    fun requestRemoveMeal(date: LocalDate, slotType: MealSlotType) {
        val meal = _uiState.value.weekDays.find { it.date == date }?.slots?.get(slotType)
        if (meal != null) {
            _uiState.update {
                it.copy(mealToRemove = MealRemovalRequest(date, slotType, meal.name))
            }
        }
    }

    fun confirmRemoveMeal() {
        val request = _uiState.value.mealToRemove ?: return

        viewModelScope.launch {
            scheduledMealDao.deleteScheduledMeal(request.date.toEpochDay(), request.slotType.name)

            // Update local state immediately
            _uiState.update { state ->
                val updatedWeekDays = state.weekDays.map { day ->
                    if (day.date == request.date) {
                        day.copy(slots = day.slots + (request.slotType to null))
                    } else {
                        day
                    }
                }
                state.copy(
                    weekDays = updatedWeekDays,
                    mealToRemove = null
                )
            }
        }
    }

    fun dismissRemovalDialog() {
        _uiState.update { it.copy(mealToRemove = null) }
    }

    // Keep for backwards compatibility with tests
    fun removeMeal(date: LocalDate, slotType: MealSlotType) {
        viewModelScope.launch {
            scheduledMealDao.deleteScheduledMeal(date.toEpochDay(), slotType.name)

            // Update local state immediately
            _uiState.update { state ->
                val updatedWeekDays = state.weekDays.map { day ->
                    if (day.date == date) {
                        day.copy(slots = day.slots + (slotType to null))
                    } else {
                        day
                    }
                }
                state.copy(weekDays = updatedWeekDays)
            }
        }
    }

    fun navigateToWeek(offset: Int) {
        val newWeekStart = WeekCalculator.navigateWeek(_currentWeekStart.value, offset)
        _currentWeekStart.value = newWeekStart
        _uiState.update {
            it.copy(
                currentWeekStart = newWeekStart,
                isLoading = true
            )
        }
    }

    fun goToCurrentWeek() {
        val currentWeekStart = WeekCalculator.getWeekStart(today)
        if (_currentWeekStart.value != currentWeekStart) {
            _currentWeekStart.value = currentWeekStart
            _uiState.update {
                it.copy(
                    currentWeekStart = currentWeekStart,
                    isLoading = true
                )
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
