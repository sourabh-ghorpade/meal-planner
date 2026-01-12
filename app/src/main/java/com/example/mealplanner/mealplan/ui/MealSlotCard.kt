package com.example.mealplanner.mealplan.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mealplanner.data.model.Meal
import com.example.mealplanner.data.model.MealSlotType
import com.example.mealplanner.ui.theme.MealPlannerTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MealSlotCard(
    slotType: MealSlotType,
    meal: Meal?,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val description = if (meal != null) {
        "${slotType.displayName}: ${meal.name}. Long press to remove."
    } else {
        "${slotType.displayName}: Empty. Tap to add meal."
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .semantics {
                contentDescription = description
                role = Role.Button
            }
            .combinedClickable(
                onClick = onClick,
                onLongClick = if (meal != null) onRemove else null
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (meal != null) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (meal != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = slotType.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = meal.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = slotType.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Add ${slotType.displayName}",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MealSlotCardEmptyPreview() {
    MealPlannerTheme {
        MealSlotCard(
            slotType = MealSlotType.BREAKFAST,
            meal = null,
            onClick = {},
            onRemove = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MealSlotCardFilledPreview() {
    MealPlannerTheme {
        MealSlotCard(
            slotType = MealSlotType.LUNCH,
            meal = Meal(1L, "Grilled Chicken Salad"),
            onClick = {},
            onRemove = {}
        )
    }
}
