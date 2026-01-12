package com.example.mealplanner.mealplan.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
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
fun DayColumn(
    day: DayWithMeals,
    onSlotClick: (MealSlotType) -> Unit,
    onMealRemove: (MealSlotType) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayFormatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())
    val dateFormatter = DateTimeFormatter.ofPattern("d", Locale.getDefault())

    val backgroundColor = when {
        day.isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        day.isPast -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.surface
    }

    val textAlpha = if (day.isPast) 0.6f else 1f

    Column(
        modifier = modifier
            .width(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Day header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = day.date.format(dayFormatter).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (day.isToday) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = textAlpha)
                }
            )
            Text(
                text = day.date.format(dateFormatter),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (day.isToday) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = textAlpha)
                }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Meal slots
        MealSlotType.entries.forEach { slotType ->
            MealSlotCard(
                slotType = slotType,
                meal = day.slots[slotType],
                onClick = { onSlotClick(slotType) },
                onRemove = { onMealRemove(slotType) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DayColumnPreview() {
    MealPlannerTheme {
        DayColumn(
            day = DayWithMeals(
                date = LocalDate.of(2026, 1, 12),
                isPast = false,
                isToday = true,
                slots = mapOf(
                    MealSlotType.BREAKFAST to Meal(1L, "Oatmeal"),
                    MealSlotType.LUNCH to null,
                    MealSlotType.DINNER to Meal(2L, "Grilled Salmon")
                )
            ),
            onSlotClick = {},
            onMealRemove = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DayColumnPastPreview() {
    MealPlannerTheme {
        DayColumn(
            day = DayWithMeals(
                date = LocalDate.of(2026, 1, 10),
                isPast = true,
                isToday = false,
                slots = MealSlotType.entries.associateWith { null }
            ),
            onSlotClick = {},
            onMealRemove = {}
        )
    }
}
