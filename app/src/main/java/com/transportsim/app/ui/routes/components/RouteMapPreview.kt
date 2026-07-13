package com.transportsim.app.ui.routes.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.Route

@Composable
fun RouteMapPreview(
    route: Route,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
    ) {
        val width = size.width
        val height = size.height
        
        // Background
        drawRect(color = Color(0xFF07101A))
        
        // Draw grid
        val gridColor = Color(0xFF1A2A3A)
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
        
        // Draw route path
        val points = listOf(
            Offset(width * 0.05f, height * 0.6f),
            Offset(width * 0.2f, height * 0.45f),
            Offset(width * 0.4f, height * 0.5f),
            Offset(width * 0.6f, height * 0.4f),
            Offset(width * 0.8f, height * 0.45f),
            Offset(width * 0.95f, height * 0.3f)
        )
        
        // Glow
        for (i in 0 until points.size - 1) {
            drawLine(
                color = Cyan.copy(alpha = 0.15f),
                start = points[i],
                end = points[i + 1],
                strokeWidth = 20f,
                cap = StrokeCap.Round
            )
        }
        
        // Main line
        for (i in 0 until points.size - 1) {
            drawLine(
                color = Cyan,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
        
        // Waypoint dots (simplified)
        listOf(0, 2, 4).forEach { i ->
            if (i < points.size) {
                val color = when (i) {
                    0 -> Green
                    points.size - 1 -> Red
                    else -> Cyan
                }
                drawCircle(
                    color = color,
                    radius = 6f,
                    center = points[i]
                )
                drawCircle(
                    color = color.copy(alpha = 0.3f),
                    radius = 14f,
                    center = points[i]
                )
            }
        }
    }
}