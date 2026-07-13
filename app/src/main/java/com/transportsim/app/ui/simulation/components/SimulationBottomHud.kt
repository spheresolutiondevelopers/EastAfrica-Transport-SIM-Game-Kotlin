package com.transportsim.app.ui.simulation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.transportsim.app.ui.theme.*

@Composable
fun SimulationBottomHud(
    speed: Float,
    gear: Int,
    fuelPercent: Float,
    engineTemp: Float,
    satisfactionPercent: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        // Speedometer + Gear
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Speedometer(
                speed = speed,
                maxSpeed = 120f,
                modifier = Modifier.size(100.dp)
            )
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (speed < 1) "N" else "$gear",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = Gold
                )
                Text(
                    text = "GEAR",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Status bars
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            StatusBar(
                label = "FUEL",
                value = fuelPercent,
                color = if (fuelPercent < 20) Red else Gold
            )
            StatusBar(
                label = "ENG",
                value = (engineTemp - 60) / 60 * 100,
                color = if (engineTemp > 95) Red else Cyan
            )
            StatusBar(
                label = "SAT",
                value = satisfactionPercent,
                color = Green
            )
        }
    }
}

@Composable
fun Speedometer(
    speed: Float,
    maxSpeed: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val centerX = size.width / 2
        val centerY = size.height * 0.65f
        val radius = size.width * 0.4f
        
        // Background arc
        drawArc(
            color = Color.White.copy(alpha = 0.1f),
            startAngle = 135f,
            sweepAngle = 270f,
            useCenter = false,
            topLeft = Offset(centerX - radius, centerY - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8f, cap = StrokeCap.Round)
        )
        
        // Speed arc
        val progress = (speed / maxSpeed).coerceIn(0f, 1f)
        val sweepAngle = 270f * progress
        val color = when {
            progress < 0.4f -> Green
            progress < 0.7f -> Gold
            else -> Red
        }
        
        drawArc(
            color = color,
            startAngle = 135f,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(centerX - radius, centerY - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8f, cap = StrokeCap.Round)
        )
        
        // Speed text
        drawContext.canvas.nativeCanvas.apply {
            val paint = android.graphics.Paint().apply {
                textSize = 28f
                color = android.graphics.Color.WHITE
                textAlign = android.graphics.Paint.Align.CENTER
                isFakeBoldText = true
            }
            drawText(
                "${speed.toInt()}",
                centerX,
                centerY + 10f,
                paint
            )
            val unitPaint = android.graphics.Paint().apply {
                textSize = 10f
                color = android.graphics.Color.WHITE
                textAlign = android.graphics.Paint.Align.CENTER
                alpha = 150
            }
            drawText(
                "KM/H",
                centerX,
                centerY + 32f,
                unitPaint
            )
        }
    }
}

@Composable
fun StatusBar(
    label: String,
    value: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .width(140.dp)
            .height(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(30.dp)
        )
        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
        ) {
            drawRoundRect(
                color = MaterialTheme.colorScheme.surfaceVariant,
                radius = 2f
            )
            drawRoundRect(
                color = color,
                topLeft = Offset(0f, 0f),
                size = androidx.compose.ui.geometry.Size(
                    size.width * (value / 100f).coerceIn(0f, 1f),
                    size.height
                ),
                radius = 2f
            )
        }
        Text(
            text = "${value.toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(32.dp)
        )
    }
}