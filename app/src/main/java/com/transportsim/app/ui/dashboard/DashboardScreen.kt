package com.transportsim.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.transportsim.app.ui.dashboard.components.*
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.VehicleCategory
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    onVehicleSelected: (Int) -> Unit,
    onRouteSelected: (String) -> Unit,
    onMissionSelected: (String) -> Unit,
    onStartSimulation: (String, Int) -> Unit,
    onStartTraining: () -> Unit,
    onNavigateToFleet: () -> Unit,
    onNavigateToRoutes: () -> Unit,
    onNavigateToMissions: () -> Unit,
    onNavigateToGarage: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isMuted by viewModel.isMuted.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf<VehicleCategory?>(VehicleCategory.BUS) }
    
    val configuration = LocalConfiguration.current
    val isSmallHeight = configuration.screenHeightDp < 520
    val sidebarWidth = if (isSmallHeight) 190.dp else 220.dp
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MobileNavDrawer(
                currentScreen = "dashboard",
                onScreenSelected = { screen ->
                    when (screen) {
                        "fleet" -> onNavigateToFleet()
                        "routes" -> onNavigateToRoutes()
                        "missions" -> onNavigateToMissions()
                        "garage" -> onNavigateToGarage()
                        "settings" -> onNavigateToSettings()
                    }
                },
                balance = uiState.playerBalance,
                level = uiState.playerLevel,
                xp = 3450, // Example XP value matching design
                onClose = {
                    scope.launch { drawerState.close() }
                }
            )
        },
        gesturesEnabled = isSmallHeight
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDeep)
        ) {
            // High-Fidelity HUD
            DashboardHUD(
                balance = uiState.playerBalance,
                fleetSize = 12, // From design
                activeSize = 9,  // From design
                level = uiState.playerLevel,
                currentScreen = "dashboard",
                onScreenSelected = { screen ->
                    when (screen) {
                        "fleet" -> onNavigateToFleet()
                        "routes" -> onNavigateToRoutes()
                        "missions" -> onNavigateToMissions()
                        "garage" -> onNavigateToGarage()
                        "settings" -> onNavigateToSettings()
                    }
                },
                onMenuClick = {
                    scope.launch { drawerState.open() }
                },
                modifier = Modifier.height(if (isSmallHeight) 46.dp else 58.dp)
            )

            // Main 3-Column Content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // LEFT: Vehicle Category Browser
                DashLeft(
                    categories = uiState.categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { category ->
                        selectedCategory = category
                    },
                    modifier = Modifier.width(sidebarWidth)
                )

                // CENTER: Map + Overlays
                DashCenter(
                    routeId = uiState.selectedRouteId,
                    mapVehicles = uiState.mapVehicles,
                    selectedVehicle = uiState.vehicles.find { 
                        it.typeId.startsWith(selectedCategory?.name?.lowercase() ?: "") 
                    } ?: uiState.vehicles.firstOrNull(),
                    isMuted = isMuted,
                    onToggleAudio = { viewModel.toggleMute() },
                    onStartSimulation = {
                        if (uiState.selectedRouteId != null && uiState.selectedVehicleId != null) {
                            onStartSimulation(uiState.selectedRouteId!!, uiState.selectedVehicleId!!)
                        }
                    },
                    onStartTraining = onStartTraining,
                    modifier = Modifier.weight(1f)
                )

                // RIGHT: Performance & Status
                DashRight(
                    revenue = uiState.todayRevenue,
                    passengers = uiState.todayPassengers,
                    cargo = uiState.todayCargo,
                    onTimeRate = uiState.todayOnTimeRate,
                    routeStatuses = uiState.routeStatuses,
                    fuelAlerts = uiState.fuelAlerts,
                    modifier = Modifier.width(sidebarWidth)
                )
            }
        }
    }
}
