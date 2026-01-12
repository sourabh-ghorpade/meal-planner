package com.example.mealplanner.mealplan.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.mealplanner.data.model.Meal
import com.example.mealplanner.mealplan.SelectedSlot
import com.example.mealplanner.ui.components.EmptyState
import androidx.compose.ui.tooling.preview.Preview
import com.example.mealplanner.data.model.MealSlotType
import com.example.mealplanner.ui.theme.MealPlannerTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPickerBottomSheet(
    meals: List<Meal>,
    selectedSlot: SelectedSlot,
    onMealSelected: (Meal) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Select Meal for",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${selectedSlot.date.format(dateFormatter)} ${selectedSlot.slotType.displayName}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider()

            if (meals.isEmpty()) {
                EmptyState(
                    icon = Icons.Filled.Info,
                    title = "No meals available",
                    description = "Add some meals to start planning your week"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(meals, key = { it.id }) { meal ->
                        ListItem(
                            headlineContent = {
                                Text(text = meal.name)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onMealSelected(meal) }
                                .semantics {
                                    contentDescription = "Select ${meal.name}"
                                    role = Role.Button
                                }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MealPickerBottomSheetContentPreview() {
    val meals = listOf(
        Meal(1L, "Oatmeal"),
        Meal(2L, "Grilled Chicken Salad"),
        Meal(3L, "Pasta Primavera"),
        Meal(4L, "Salmon with Rice")
    )
    val selectedSlot = SelectedSlot(
        date = LocalDate.of(2026, 1, 12),
        slotType = MealSlotType.LUNCH
    )

    MealPlannerTheme {
        // Note: ModalBottomSheet cannot be previewed directly,
        // so we preview the content structure
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Select Meal for",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Monday Lunch",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            HorizontalDivider()
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(meals, key = { it.id }) { meal ->
                    ListItem(
                        headlineContent = { Text(text = meal.name) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
