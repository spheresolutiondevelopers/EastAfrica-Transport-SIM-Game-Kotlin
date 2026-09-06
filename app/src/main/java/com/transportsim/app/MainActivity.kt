package com.transportsim.app

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.transportsim.app.ui.app.AppNavHost
import com.transportsim.app.ui.app.AppState
import com.transportsim.app.ui.app.Destinations
import com.transportsim.app.ui.app.MainViewModel
import com.transportsim.app.ui.app.rememberAppState
import com.transportsim.app.ui.components.BottomNavBar
import com.transportsim.app.ui.dashboard.components.DashboardHUD
import com.transportsim.app.ui.dashboard.components.MobileNavDrawer
import com.transportsim.app.ui.theme.TransportSimTheme
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.filament.Engine
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import kotlinx.coroutines.launch

val LocalSceneEngine = compositionLocalOf<Engine> {
    error("No SceneView Engine provided")
}

val LocalModelLoader = compositionLocalOf<ModelLoader> {
    error("No ModelLoader provided")
}

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
fun TransportSimApp(
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appState = rememberAppState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Shared 3D Engine and Loader
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)

    val configuration = LocalConfiguration.current
    val isSmallHeight = configuration.screenHeightDp < 520

    CompositionLocalProvider(
        LocalSceneEngine provides engine,
        LocalModelLoader provides modelLoader
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                MobileNavDrawer(
                    currentScreen = appState.currentRoute ?: Destinations.DASHBOARD,
                    onScreenSelected = { screen ->
                        appState.navigateTo(screen)
                        scope.launch { drawerState.close() }
                    },
                    balance = uiState.balance,
                    level = uiState.level,
                    xp = uiState.xp,
                    onClose = {
                        scope.launch { drawerState.close() }
                    }
                )
            },
            gesturesEnabled = appState.currentRoute == Destinations.DASHBOARD || isSmallHeight
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                contentWindowInsets = WindowInsets(0, 0, 0, 0), // Ignore default insets for full screen
                topBar = {
                    if (appState.currentRoute !in listOf(Destinations.SIMULATION, Destinations.SIMULATION_ROUTE, Destinations.ONBOARDING)) {
                        DashboardHUD(
                            balance = uiState.balance,
                            fleetSize = uiState.fleetSize,
                            activeSize = uiState.activeSize,
                            level = uiState.level,
                            currentScreen = appState.currentRoute ?: Destinations.DASHBOARD,
                            onScreenSelected = { screen ->
                                appState.navigateTo(screen)
                            },
                            onMenuClick = {
                                scope.launch { drawerState.open() }
                            },
                            modifier = Modifier.height(if (isSmallHeight) 46.dp else 58.dp)
                        )
                    }
                }
            ) { paddingValues ->
                AppNavHost(
                    appState = appState,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}
