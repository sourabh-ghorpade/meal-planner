package com.example.mealplanner.mealplan.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.mealplanner.data.model.MealSlotType
import com.example.mealplanner.mealplan.DayWithMeals
import androidx.compose.ui.tooling.preview.Preview
import com.example.mealplanner.data.model.Meal
import com.example.mealplanner.ui.theme.MealPlannerTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun WeekView(
    weekStart: LocalDate,
    days: List<DayWithMeals>,
    onNavigateWeek: (offset: Int) -> Unit,
    onGoToToday: () -> Unit,
    onSlotClick: (date: LocalDate, slotType: MealSlotType) -> Unit,
    onMealRemove: (date: LocalDate, slotType: MealSlotType) -> Unit,
    modifier: Modifier = Modifier
) {
    val weekEnd = weekStart.plusDays(6)
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())
    val weekLabel = "${weekStart.format(dateFormatter)} - ${weekEnd.format(dateFormatter)}, ${weekStart.year}"

    Column(modifier = modifier.fillMaxWidth()) {
        // Navigation header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onNavigateWeek(-1) },
                modifier = Modifier.semantics {
                    contentDescription = "Previous week"
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null
                )
            }

            Text(
                text = weekLabel,
                style = MaterialTheme.typography.titleMedium
            )

            IconButton(
                onClick = { onNavigateWeek(1) },
                modifier = Modifier.semantics {
                    contentDescription = "Next week"
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }

        // Today button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(
                onClick = onGoToToday,
                modifier = Modifier.semantics {
                    contentDescription = "Go to current week"
                }
            ) {
                Text(text = "Today")
            }
        }

        // Days row (horizontally scrollable)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            days.forEach { day ->
                DayColumn(
                    day = day,
                    onSlotClick = { slotType -> onSlotClick(day.date, slotType) },
                    onMealRemove = { slotType -> onMealRemove(day.date, slotType) }
                )
            }
            // Add some padding at the end for better scrolling UX
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeekViewPreview() {
    val today = LocalDate.of(2026, 1, 12)
    val weekStart = today.minusDays(today.dayOfWeek.value.toLong() - 1)

    val days = (0..6).map { offset ->
        val date = weekStart.plusDays(offset.toLong())
        DayWithMeals(
            date = date,
            isPast = date.isBefore(today),
            isToday = date == today,
            slots = if (offset == 0) {
                mapOf(
                    MealSlotType.BREAKFAST to Meal(1L, "Pancakes"),
                    MealSlotType.LUNCH to null,
                    MealSlotType.DINNER to Meal(2L, "Pasta")
                )
            } else {
                MealSlotType.entries.associateWith { null }
            }
        )
    }

    MealPlannerTheme {
        WeekView(
            weekStart = weekStart,
            days = days,
            onNavigateWeek = {},
            onGoToToday = {},
            onSlotClick = { _, _ -> },
            onMealRemove = { _, _ -> }
        )
    }
}
