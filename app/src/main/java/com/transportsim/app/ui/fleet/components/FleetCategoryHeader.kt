package com.transportsim.app.ui.fleet.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.transportsim.app.ui.theme.*

@Composable
fun FleetCategoryHeader(
    category: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category icon and name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val icon = when (category.uppercase()) {
                "BUS" -> "🚌"
                "MATATU" -> "🚐"
                "PICKUP" -> "🛻"
                "LORRY" -> "🚛"
                "BODA" -> "🏍"
                "TAXI" -> "🚕"
                else -> "🚗"
            }
            Text(
                text = "$icon $category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = when (category.uppercase()) {
                    "BUS" -> Cyan
                    "MATATU" -> Gold
                    "PICKUP" -> Green
                    "LORRY" -> Orange
                    "BODA" -> Purple
                    "TAXI" -> Pink
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
        
        Text(
            text = "$count vehicles",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}