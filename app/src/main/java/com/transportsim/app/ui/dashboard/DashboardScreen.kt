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
    var selectedCategory by remember { mutableStateOf<VehicleCategory?>(VehicleCategory.PICKUP) }
    
    val configuration = LocalConfiguration.current
    val isSmallHeight = configuration.screenHeightDp < 520
    val sidebarWidth = if (isSmallHeight) 190.dp else 220.dp
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        // Main 3-Column Content
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // LEFT: Vehicle Category Browser
            DashLeft(
                categories = uiState.categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = category
                    viewModel.selectCategory(category)
                },
                modifier = Modifier.width(sidebarWidth)
            )

            // CENTER: Map + Overlays
            val selectedVehicle = uiState.vehicles.find { 
                it.typeId.startsWith(selectedCategory?.name?.lowercase() ?: "") 
            } ?: uiState.vehicles.firstOrNull()
            
            DashCenter(
                routeId = uiState.selectedRouteId,
                mapVehicles = uiState.mapVehicles,
                selectedVehicle = selectedVehicle,
                turntableVehicle = uiState.selectedTurntableVehicle,
                onNextTurntable = { viewModel.nextTurntableVehicle() },
                onPrevTurntable = { viewModel.previousTurntableVehicle() },
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
