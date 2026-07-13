package com.transportsim.app.ui.simulation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.transportsim.app.ui.simulation.TrafficVehicle
import com.transportsim.domain.models.RouteWaypoint
import com.transportsim.domain.models.TrafficLightPhase

@Composable
fun SimulationMiniMap(
    routeProgress: Float,
    waypoints: List<RouteWaypoint>,
    trafficVehicles: List<TrafficVehicle>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.Black.copy(alpha = 0.7f),
        shape = MaterialTheme.shapes.medium,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val padding = 12f
                val roadWidth = width - padding * 2
                val roadHeight = height - padding * 2
                
                // Route path (simplified)
                val routePoints = listOf(
                    Offset(padding, height * 0.5f),
                    Offset(padding + roadWidth * 0.2f, height * 0.45f),
                    Offset(padding + roadWidth * 0.4f, height * 0.5f),
                    Offset(padding + roadWidth * 0.6f, height * 0.4f),
                    Offset(padding + roadWidth * 0.8f, height * 0.45f),
                    Offset(padding + roadWidth, height * 0.35f)
                )
                
                // Draw route
                for (i in 0 until routePoints.size - 1) {
                    drawLine(
                        color = Color(0xFF1A3A50),
                        start = routePoints[i],
                        end = routePoints[i + 1],
                        strokeWidth = 6f
                    )
                }
                for (i in 0 until routePoints.size - 1) {
                    drawLine(
                        color = Cyan.copy(alpha = 0.7f),
                        start = routePoints[i],
                        end = routePoints[i + 1],
                        strokeWidth = 2f,
                        style = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }
                
                // Player position
                val progressIndex = (routeProgress * (routePoints.size - 1)).toInt()
                val progressFrac = (routeProgress * (routePoints.size - 1)) % 1
                val idx1 = progressIndex.coerceAtMost(routePoints.size - 2)
                val idx2 = idx1 + 1
                val px = routePoints[idx1].x + (routePoints[idx2].x - routePoints[idx1].x) * progressFrac
                val py = routePoints[idx1].y + (routePoints[idx2].y - routePoints[idx1].y) * progressFrac
                
                drawCircle(
                    color = Green.copy(alpha = 0.5f),
                    radius = 8f,
                    center = Offset(px, py)
                )
                drawCircle(
                    color = Green,
                    radius = 4f,
                    center = Offset(px, py)
                )
                
                // Waypoints
                routePoints.forEachIndexed { index, point ->
                    val color = when (index) {
                        0 -> Green
                        routePoints.size - 1 -> Red
                        else -> Cyan.copy(alpha = 0.7f)
                    }
                    drawCircle(
                        color = color,
                        radius = 3f,
                        center = point
                    )
                }
                
                // Traffic vehicles (simplified dots)
                trafficVehicles.take(5).forEach { vehicle ->
                    val vx = padding + (vehicle.progress * roadWidth)
                    val vy = padding + (0.5f + (vehicle.lane * 0.05f)) * roadHeight
                    drawCircle(
                        color = Color.White.copy(alpha = 0.4f),
                        radius = 2f,
                        center = Offset(vx, vy)
                    )
                }
            }
            
            // Label
            Text(
                text = "▲ LIVE MAP",
                style = MaterialTheme.typography.labelSmall,
                color = Cyan.copy(alpha = 0.7f),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
            )
        }
    }
}