package com.transportsim.app.ui.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.transportsim.app.ui.theme.*

@Composable
fun SettingsToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(44.dp, 24.dp)
            .clip(CircleShape)
            .clickable { onCheckedChange(!checked) }
            .then(
                if (checked) {
                    Modifier.background(Cyan)
                } else {
                    Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                }
            )
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .align(
                    if (checked) Alignment.CenterEnd else Alignment.CenterStart
                )
                .then(
                    if (checked) {
                        Modifier.padding(end = 3.dp)
                    } else {
                        Modifier.padding(start = 3.dp)
                    }
                )
        )
    }
}