package com.transportsim.app.ui.routes.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.Route
import com.transportsim.domain.models.TerrainType

@Composable
fun RouteCard(
    route: Route,
    isSelected: Boolean = false,
    isLocked: Boolean = false,
    onClick: () -> Unit,
    onAssign: () -> Unit,
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val terrainColor = when (route.terrainType) {
        TerrainType.URBAN -> Cyan
        TerrainType.HIGHWAY -> Purple
        TerrainType.EXPRESSWAY -> Teal
        TerrainType.RURAL -> Green
        TerrainType.COASTAL -> Gold
    }
    
    Card(
        modifier = modifier
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) terrainColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = route.routeId,
                        style = MaterialTheme.typography.labelMedium,
                        color = terrainColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${route.distanceKm} km",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Route name
                Text(
                    text = route.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isLocked) 
                        MaterialTheme.colorScheme.onSurfaceVariant 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                
                // Terrain tags
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TerrainTag(
                        label = route.terrainType.name,
                        color = terrainColor
                    )
                    route.terrainTags.take(2).forEach { tag ->
                        TerrainTag(
                            label = tag,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
                
                // Stats
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    RouteStat(
                        label = "Assigned",
                        value = "${(1..3).random()} vehicles"
                    )
                    RouteStat(
                        label = "Revenue",
                        value = "KSH ${(2000..8000).random()}/day"
                    )
                }
            }
            
            // Action button
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isLocked) {
                    Button(
                        onClick = onUnlock,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold.copy(alpha = 0.2f),
                            contentColor = Gold
                        ),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(
                            text = "🔒 Unlock",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = onAssign,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Green.copy(alpha = 0.2f),
                            contentColor = Green
                        ),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(
                            text = "+ Assign",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                if (isSelected) {
                    Surface(
                        color = Green.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Green)
                    ) {
                        Text(
                            text = "ACTIVE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Green,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TerrainTag(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun RouteStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}