package com.example.fitplate.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class FilterState(
    val dietaryPreference: String = "All",
    val mealType: String = "All",
    val intolerance: String = "None",
    val cookingTime: String = "Any",
    val dietType: String = "All" // All, Veg, Non-Veg
)

@Composable
fun FilterDialog(
    initialFilterState: FilterState,
    onDismiss: () -> Unit,
    onApply: (FilterState) -> Unit
) {
    var filterState by remember { mutableStateOf(initialFilterState) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filters",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Content
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    FilterSection(
                        title = "Diet Type",
                        options = listOf("All", "Veg", "Non-Veg"),
                        selectedOption = filterState.dietType,
                        onOptionSelected = { filterState = filterState.copy(dietType = it) }
                    )

                    FilterSection(
                        title = "Dietary Preference",
                        options = listOf("All", "Vegetarian", "Vegan", "Pescetarian", "Paleo"),
                        selectedOption = filterState.dietaryPreference,
                        onOptionSelected = { filterState = filterState.copy(dietaryPreference = it) }
                    )

                    FilterSection(
                        title = "Meal Type",
                        options = listOf("All", "Main Course", "Breakfast", "Snack", "Dessert", "Salad"),
                        selectedOption = filterState.mealType,
                        onOptionSelected = { filterState = filterState.copy(mealType = it) }
                    )

                    FilterSection(
                        title = "Intolerances",
                        options = listOf("None", "Dairy", "Gluten", "Peanut", "Soy"),
                        selectedOption = filterState.intolerance,
                        onOptionSelected = { filterState = filterState.copy(intolerance = it) }
                    )

                    FilterSection(
                        title = "Cooking Time",
                        options = listOf("Any", "15 min", "30 min", "45 min", "60 min"),
                        selectedOption = filterState.cookingTime,
                        onOptionSelected = { filterState = filterState.copy(cookingTime = it) }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { filterState = FilterState() },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = "Clear All",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Button(
                        onClick = { onApply(filterState) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = CircleShape,
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 32.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Apply Filters",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterSection(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEach { option ->
                val isSelected = option == selectedOption
                val isNonVeg = option == "Non-Veg"
                
                val containerColor = when {
                    isSelected && isNonVeg -> Color(0xFFFF4B4B).copy(alpha = 0.2f)
                    isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else -> MaterialTheme.colorScheme.surfaceContainer
                }
                
                val borderColor = when {
                    isSelected && isNonVeg -> Color(0xFFFF4B4B)
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.surfaceContainerHighest
                }
                
                val textColor = when {
                    isSelected && isNonVeg -> Color(0xFFFF4B4B)
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(containerColor)
                        .border(1.dp, borderColor, CircleShape)
                        .clickable { onOptionSelected(option) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.labelMedium,
                        color = textColor
                    )
                }
            }
        }
    }
}
