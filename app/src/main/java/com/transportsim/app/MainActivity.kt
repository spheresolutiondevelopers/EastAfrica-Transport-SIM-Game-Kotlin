package com.transportsim.app

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.transportsim.app.ui.app.AppNavHost
import com.transportsim.app.ui.app.AppState
import com.transportsim.app.ui.app.rememberAppState
import com.transportsim.app.ui.components.BottomNavBar
import com.transportsim.app.ui.theme.TransportSimTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Force Landscape as per design requirements
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        
        enableEdgeToEdge()
        
        // Hide system bars for immersive feel
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        
        setContent {
            TransportSimTheme {
                TransportSimApp()
            }
        }
    }
}

@Composable
fun TransportSimApp() {
    val context = LocalContext.current
    val appState = rememberAppState()
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0), // Ignore default insets for full screen
        bottomBar = {
            if (appState.shouldShowBottomBar()) {
                // BottomNavBar(appState = appState) 
                // Note: The design uses a custom HUD top nav, 
                // we might want to disable standard bottom bar on Dashboard
            }
        }
    ) { paddingValues ->
        AppNavHost(
            appState = appState,
            modifier = Modifier.padding(paddingValues)
        )
    }
}
