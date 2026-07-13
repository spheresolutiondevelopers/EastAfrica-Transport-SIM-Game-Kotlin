package com.transportsim.app.ui.simulation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.transportsim.app.ui.theme.*

@Composable
fun SimulationTopHud(
    speed: Float,
    passengers: Int,
    distance: Float,
    routeName: String,
    isDayMode: Boolean,
    onToggleDayNight: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Speed, Passengers, Distance
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatPill(
                value = "${speed.toInt()}",
                unit = "KM/H",
                color = Cyan
            )
            StatPill(
                value = "$passengers",
                unit = "PAX",
                color = Green
            )
            StatPill(
                value = "${"%.1f".format(distance)}",
                unit = "KM",
                color = Gold
            )
        }
        
        // Center: Route name
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            shape = MaterialTheme.shapes.medium,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Gold.copy(alpha = 0.3f)
            )
        ) {
            Text(
                text = routeName,
                style = MaterialTheme.typography.labelSmall,
                color = Gold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
        
        // Right: Day/Night toggle and Exit
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onToggleDayNight,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    contentColor = Gold
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = if (isDayMode) "☀ DAY" else "🌙 NIGHT",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Button(
                onClick = onExit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Red.copy(alpha = 0.2f),
                    contentColor = Red
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "✕ EXIT",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StatPill(
    value: String,
    unit: String,
    color: Color
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        shape = MaterialTheme.shapes.medium,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            color.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}