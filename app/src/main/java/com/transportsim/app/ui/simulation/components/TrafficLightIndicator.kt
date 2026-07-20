package com.transportsim.app.ui.simulation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.TrafficLightPhase

@Composable
fun TrafficLightIndicator(
    phase: TrafficLightPhase,
    distanceM: Float,
    timerSeconds: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
        shape = MaterialTheme.shapes.medium,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "TRAFFIC LIGHT",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 2.sp
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TrafficLightBulb(
                    isOn = phase == TrafficLightPhase.RED,
                    color = Red
                )
                TrafficLightBulb(
                    isOn = phase == TrafficLightPhase.AMBER,
                    color = Gold
                )
                TrafficLightBulb(
                    isOn = phase == TrafficLightPhase.GREEN,
                    color = Green
                )
            }
            
            Text(
                text = phase.name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = when (phase) {
                    TrafficLightPhase.GREEN -> Green
                    TrafficLightPhase.AMBER -> Gold
                    TrafficLightPhase.RED -> Red
                }
            )
            
            Text(
                text = "${distanceM.toInt()}m ahead · ${timerSeconds}s",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TrafficLightBulb(
    isOn: Boolean,
    color: Color
) {
    Box(
        modifier = Modifier
            .size(18.dp)
    ) {
        androidx.compose.foundation.Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val radius = size.width / 2
            drawCircle(
                color = if (isOn) color else color.copy(alpha = 0.15f),
                radius = radius
            )
            if (isOn) {
                drawCircle(
                    color = color.copy(alpha = 0.5f),
                    radius = radius * 2.5f,
                    center = center
                )
            }
        }
    }
}