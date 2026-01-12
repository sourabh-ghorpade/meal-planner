package com.example.mealplanner.mealplan.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mealplanner.mealplan.MealPlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanScreen(
    viewModel: MealPlanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show error snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Planner") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading && uiState.weekDays.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                WeekView(
                    weekStart = uiState.currentWeekStart,
                    days = uiState.weekDays,
                    onNavigateWeek = viewModel::navigateToWeek,
                    onGoToToday = viewModel::goToCurrentWeek,
                    onSlotClick = viewModel::selectSlot,
                    onMealRemove = viewModel::requestRemoveMeal
                )
            }

            // Meal picker bottom sheet
            uiState.selectedSlot?.let { selectedSlot ->
                MealPickerBottomSheet(
                    meals = uiState.availableMeals,
                    selectedSlot = selectedSlot,
                    onMealSelected = viewModel::assignMeal,
                    onDismiss = viewModel::dismissPicker
                )
            }

            // Meal removal confirmation dialog
            uiState.mealToRemove?.let { removalRequest ->
                AlertDialog(
                    onDismissRequest = viewModel::dismissRemovalDialog,
                    title = { Text("Remove Meal") },
                    text = {
                        Text("Remove ${removalRequest.mealName} from ${removalRequest.slotType.displayName}?")
                    },
                    confirmButton = {
                        TextButton(onClick = viewModel::confirmRemoveMeal) {
                            Text("Remove")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = viewModel::dismissRemovalDialog) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
