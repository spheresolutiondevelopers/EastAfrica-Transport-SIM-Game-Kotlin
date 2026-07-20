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
import androidx.compose.ui.unit.sp
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.Route
import com.transportsim.domain.models.RouteStats
import com.transportsim.domain.models.TerrainType

@Composable
fun RouteCard(
    route: Route,
    stats: RouteStats? = null,
    isSelected: Boolean = false,
    isLocked: Boolean = false,
    onClick: () -> Unit,
    onAssign: () -> Unit,
    onUnlock: () -> Unit,
    onStart: () -> Unit,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = route.id,
                                style = MaterialTheme.typography.labelMedium,
                                color = terrainColor,
                                fontWeight = FontWeight.Bold
                            )
                            if (route.isDlc) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = Gold.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Gold)
                                ) {
                                    Text(
                                        text = "💎 DLC",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Gold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
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
                }
                
                // Action button
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    when {
                        isLocked -> {
                            Button(
                                onClick = onUnlock,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Gold.copy(alpha = 0.2f),
                                    contentColor = Gold
                                ),
                                shape = RoundedCornerShape(5.dp)
                            ) {
                                Text(
                                    text = "🔒 Unlock (Lv.${route.unlockLevel})",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        stats != null && stats.hasBeenPlayed -> {
                            Button(
                                onClick = onStart,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Green,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(5.dp)
                            ) {
                                Text(
                                    text = "▶ Play Again",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        else -> {
                            Button(
                                onClick = onStart,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Cyan,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(5.dp)
                            ) {
                                Text(
                                    text = "▶ Start Route",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Description
            Text(
                text = route.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
                maxLines = 2
            )

            // Player stats
            if (stats != null && stats.hasBeenPlayed) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RouteStatChip("🏆", "${stats.bestScore} pts")
                    RouteStatChip("⭐", "${stats.stars} stars")
                    RouteStatChip("⏱", stats.formattedFastestTime)
                    RouteStatChip("🚀", "${stats.timesCompleted}x")
                }
            } else if (!isLocked) {
                // Revenue info if not played yet
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    RouteStat(
                        label = "Potential Revenue",
                        value = "KSH ${route.revenuePerDayKsh}/day"
                    )
                }
            }
        }
    }
}

@Composable
fun RouteStatChip(
    icon: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp, 
            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = icon, fontSize = 10.sp)
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
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