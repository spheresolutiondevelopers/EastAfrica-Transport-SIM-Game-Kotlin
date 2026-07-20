package com.transportsim.app.ui.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.transportsim.app.ui.theme.Cyan

@Composable
fun ThemeToggleButton(modifier: Modifier = Modifier) {
    var isLight by remember { mutableStateOf(false) }
    Button(
        onClick = {
            isLight = !isLight
            // Toggle theme logic
        },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = if (isLight) "☀" else "🌙",
                fontSize = 14.sp
            )
            Text(
                text = if (isLight) "LIGHT" else "DARK",
                style = MaterialTheme.typography.labelSmall,
                color = Cyan
            )
        }
    }
}
