package com.transportsim.app.ui.simulation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.transportsim.app.ui.theme.*

@Composable
fun SimulationControls(
    onAccelerate: () -> Unit,
    onBrake: () -> Unit,
    onSteerLeft: () -> Unit,
    onSteerRight: () -> Unit,
    onHorn: () -> Unit,
    onRelease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Accelerate button - right side
        ControlButton(
            label = "▲\nACCEL",
            onClick = onAccelerate,
            onRelease = onRelease,
            color = Green,
            modifier = Modifier
                .align(Alignment.CenterRight)
                .padding(end = 18.dp)
                .size(80.dp, 120.dp)
        )
        
        // Brake button - left side
        ControlButton(
            label = "⬛\nBRAKE",
            onClick = onBrake,
            onRelease = onRelease,
            color = Red,
            modifier = Modifier
                .align(Alignment.CenterLeft)
                .padding(start = 18.dp)
                .size(80.dp, 120.dp)
        )
        
        // Steer Left - bottom left
        ControlButton(
            label = "◀\nLEFT",
            onClick = onSteerLeft,
            onRelease = onRelease,
            color = Cyan,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 18.dp, bottom = 100.dp)
                .size(90.dp, 80.dp)
        )
        
        // Steer Right - bottom right
        ControlButton(
            label = "▶\nRIGHT",
            onClick = onSteerRight,
            onRelease = onRelease,
            color = Cyan,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 18.dp, bottom = 100.dp)
                .size(90.dp, 80.dp)
        )
        
        // Horn - bottom center
        ControlButton(
            label = "📯\nHORN",
            onClick = onHorn,
            onRelease = onRelease,
            color = Gold,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
                .size(70.dp, 70.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
fun ControlButton(
    label: String,
    onClick: () -> Unit,
    onRelease: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    Surface(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        isPressed = true
                        onClick()
                    },
                    onDragEnd = {
                        isPressed = false
                        onRelease()
                    },
                    onDragCancel = {
                        isPressed = false
                        onRelease()
                    }
                )
            },
        color = if (isPressed) {
            color.copy(alpha = 0.3f)
        } else {
            Color.Black.copy(alpha = 0.6f)
        },
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            if (isPressed) color else color.copy(alpha = 0.5f)
        ),
        shadowElevation = if (isPressed) 0.dp else 4.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}