package com.transportsim.app.ui.simulation.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun CollisionFlash(
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(50)),
        exit = fadeOut(animationSpec = tween(250))
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Red.copy(alpha = 0.45f))
        )
    }
}