package com.transportsim.app.ui.fleet.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.transportsim.domain.models.VehicleCategory

@Composable
fun FleetFilterBar(
    selectedCategory: VehicleCategory?,
    onCategorySelected: (VehicleCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    val allCategories = listOf(null) + VehicleCategory.values().toList()
    
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(allCategories) { category ->
            AssistChip(
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = category?.name ?: "All",
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = selectedCategory == category,
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (selectedCategory == category)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = if (selectedCategory == category)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}