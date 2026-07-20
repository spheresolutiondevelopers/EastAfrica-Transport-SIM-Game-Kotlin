package com.transportsim.app.ui.garage.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.transportsim.app.ui.garage.InventoryItem
import com.transportsim.app.ui.garage.Livery
import com.transportsim.app.ui.theme.*

@Composable
fun GarageInventoryPanel(
    inventory: List<InventoryItem>,
    liveries: List<Livery>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Parts Inventory
        Text(
            text = "Parts Inventory",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                inventory.forEach { item ->
                    InventoryItemRow(item)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Liveries
        Text(
            text = "Liveries Unlocked",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Livery grid
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            liveries.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    row.forEach { livery ->
                        LiveryItem(livery = livery, modifier = Modifier.weight(1f))
                    }
                    if (row.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryItemRow(item: InventoryItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(item.icon)
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = "×${item.quantity}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = if (item.quantity > 0) Gold else Red
        )
    }
}

@Composable
fun LiveryItem(
    livery: Livery,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(36.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (livery.isLocked)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        ),
        border = if (!livery.isLocked)
            androidx.compose.foundation.BorderStroke(1.dp, Color(android.graphics.Color.parseColor(livery.color)).copy(alpha = 0.5f))
        else
            androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (livery.isLocked) {
                Text(
                    text = "🔒",
                    style = MaterialTheme.typography.labelSmall
                )
            } else {
                Text(
                    text = livery.name,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}