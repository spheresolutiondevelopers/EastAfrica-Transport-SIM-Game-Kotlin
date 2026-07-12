package com.transportsim.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
        enableEdgeToEdge()
        
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
        bottomBar = {
            if (appState.shouldShowBottomBar()) {
                BottomNavBar(appState = appState)
            }
        }
    ) { paddingValues ->
        AppNavHost(
            appState = appState,
            modifier = Modifier.padding(paddingValues)
        )
    }
}