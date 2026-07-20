package com.transportsim.app.ui.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.transportsim.app.ui.dashboard.models.MapVehicle
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.VehicleCategory

@Composable
fun MapView(
    routeId: String?,
    vehicles: List<MapVehicle>,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val bgColor = if (isDark) {
        Color(0x6605080F)
    } else {
        Color(0x66E8F0F8)
    }
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Simplified map rendering using Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                
                // Background gradient
                drawRect(color = bgColor)
                
                // Draw grid
                for (x in 0..width.toInt() step 40) {
                    drawLine(
                        color = gridColor,
                        start = Offset(x.toFloat(), 0f),
                        end = Offset(x.toFloat(), height),
                        strokeWidth = 1f
                    )
                }
                for (y in 0..height.toInt() step 40) {
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y.toFloat()),
                        end = Offset(width, y.toFloat()),
                        strokeWidth = 1f
                    )
                }
                
                // Draw route path (simplified)
                val routePoints = listOf(
                    Offset(width * 0.1f, height * 0.5f),
                    Offset(width * 0.3f, height * 0.42f),
                    Offset(width * 0.5f, height * 0.45f),
                    Offset(width * 0.7f, height * 0.4f),
                    Offset(width * 0.9f, height * 0.35f)
                )
                
                // Route glow
                for (i in 0 until routePoints.size - 1) {
                    drawLine(
                        color = Cyan.copy(alpha = 0.2f),
                        start = routePoints[i],
                        end = routePoints[i + 1],
                        strokeWidth = 16f,
                        cap = StrokeCap.Round
                    )
                }
                
                // Route line
                for (i in 0 until routePoints.size - 1) {
                    drawLine(
                        color = Cyan.copy(alpha = 0.8f),
                        start = routePoints[i],
                        end = routePoints[i + 1],
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                }
                
                // Draw vehicle dots
                vehicles.forEach { vehicle ->
                    val x = vehicle.x * width
                    val y = vehicle.y * height
                    val color = when (vehicle.category) {
                        VehicleCategory.BUS -> Cyan
                        VehicleCategory.MATATU -> Gold
                        VehicleCategory.PICKUP -> Green
                        VehicleCategory.LORRY -> Orange
                        VehicleCategory.BODA -> Purple
                        VehicleCategory.TAXI -> Pink
                    }
                    
                    // Glow
                    drawCircle(
                        color = color.copy(alpha = 0.3f),
                        radius = 18f,
                        center = Offset(x, y)
                    )
                    
                    // Dot
                    drawCircle(
                        color = color,
                        radius = 8f,
                        center = Offset(x, y)
                    )
                    
                    // Inner dot
                    drawCircle(
                        color = Color.White,
                        radius = 3f,
                        center = Offset(x, y)
                    )
                }
            }
            
            // Map controls overlay (top right)
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                MapControlButton("+") { /* Zoom in */ }
                MapControlButton("−") { /* Zoom out */ }
                MapControlButton("⌖") { /* Reset view */ }
            }
            
            // Legend (bottom left)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                LegendItem("City Bus", Cyan)
                LegendItem("Matatu", Gold)
                LegendItem("Pickup", Green)
                LegendItem("Lorry", Orange)
                LegendItem("Boda", Purple)
            }
        }
    }
}

@Composable
private fun MapControlButton(
    label: String,
    onClick: () -> Unit
) {
    androidx.compose.material3.IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(32.dp)
            .padding(2.dp)
    ) {
        androidx.compose.material3.Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = Cyan
        )
    }
}

@Composable
private fun LegendItem(
    label: String,
    color: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.foundation.Canvas(
            modifier = Modifier.size(8.dp)
        ) {
            drawCircle(color = color, radius = 4f)
        }
        androidx.compose.material3.Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Extension to check luminance if not available
private fun Color.luminance(): Float {
    val red = this.red
    val green = this.green
    val blue = this.blue
    return 0.2126f * red + 0.7152f * green + 0.0722f * blue
}
