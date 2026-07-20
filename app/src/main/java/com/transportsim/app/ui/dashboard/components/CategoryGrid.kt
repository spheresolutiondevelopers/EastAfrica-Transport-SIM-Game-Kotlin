package com.transportsim.app.ui.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.transportsim.app.ui.dashboard.models.CategoryProgress
import com.transportsim.domain.models.VehicleCategory

@Composable
fun CategoryGrid(
    categories: List<CategoryProgress>,
    onCategorySelected: (VehicleCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Fleet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val rows = categories.chunked(2)
        rows.forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { category ->
                    CategoryProgressBar(
                        category = category.category,
                        owned = category.owned,
                        total = category.total,
                        onClick = { onCategorySelected(category.category) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Add spacer if the row is not full
                if (rowItems.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            if (rowIndex < rows.size - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
