package com.transportsim.app.ui.simulation.components

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

@Composable
fun BackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit
) {
    val dispatcher = LocalOnBackPressedDispatcherOwner.current
    val callback = remember {
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                onBack()
            }
        }
    }
    
    DisposableEffect(dispatcher) {
        dispatcher?.onBackPressedDispatcher?.addCallback(callback)
        onDispose {
            callback.remove()
        }
    }
    
    callback.isEnabled = enabled
}