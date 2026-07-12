package com.transportsim.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.transportsim.app.R
import com.transportsim.app.ui.components.*
import com.transportsim.app.ui.dashboard.components.*
import com.transportsim.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onVehicleSelected: (Int) -> Unit,
    onRouteSelected: (String) -> Unit,
    onMissionSelected: (String) -> Unit,
    onStartSimulation: (String, Int) -> Unit,
    onStartTraining: () -> Unit,
    onNavigateToFleet: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TransportSim",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Cyan
                    )
                },
                actions = {
                    IconButton(onClick = { /* Navigate to notifications */ }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_notification),
                            contentDescription = "Notifications"
                        )
                    }
                    IconButton(onClick = { /* Navigate to settings */ }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_settings),
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Vehicle Categories Grid
            item {
                CategoryGrid(
                    categories = uiState.categories,
                    onCategorySelected = { category ->
                        onNavigateToFleet()
                    }
                )
            }
            
            // Level Progress
            item {
                LevelProgressCard(
                    currentLevel = uiState.playerLevel,
                    currentXp = uiState.playerXp,
                    nextLevelXp = uiState.nextLevelXp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Active Missions Preview
            if (uiState.activeMissions.isNotEmpty()) {
                item {
                    MissionsPreview(
                        missions = uiState.activeMissions,
                        onMissionSelected = onMissionSelected
                    )
                }
            }
            
            // Map with overlays (Turntable + Detail Panel)
            item {
                DashboardMapWithOverlays(
                    routeId = uiState.selectedRouteId,
                    vehicles = uiState.mapVehicles,
                    selectedVehicle = uiState.vehicles.firstOrNull(),
                    isOwned = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                )
            }
            
            // Action Buttons (START SIMULATION + TRAINING)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StartSimulationButton(
                        onClick = {
                            if (uiState.selectedRouteId != null && uiState.selectedVehicleId != null) {
                                onStartSimulation(uiState.selectedRouteId!!, uiState.selectedVehicleId!!)
                            }
                        },
                        isEnabled = uiState.selectedRouteId != null && uiState.selectedVehicleId != null,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // ✅ TRAINING BUTTON – explicitly included here
                    TrainingButton(
                        onClick = onStartTraining,
                        modifier = Modifier.weight(0.6f)
                    )
                }
            }
            
            // Quick Stats
            item {
                QuickStatsRow(
                    revenue = uiState.todayRevenue,
                    passengers = uiState.todayPassengers,
                    cargo = uiState.todayCargo,
                    onTimeRate = uiState.todayOnTimeRate,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Route Status
            item {
                RouteStatusCard(
                    routes = uiState.routeStatuses,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Fuel Alert
            item {
                FuelAlertCard(
                    alerts = uiState.fuelAlerts,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun DashboardMapWithOverlays(
    routeId: String?,
    vehicles: List<MapVehicle>,
    selectedVehicle: Vehicle?,
    isOwned: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // The map itself
        MapView(
            routeId = routeId,
            vehicles = vehicles,
            modifier = Modifier.fillMaxSize()
        )
        
        // Turntable overlay (top-left)
        VehicleTurntable(
            vehicleId = selectedVehicle?.vehicleId,
            isOwned = isOwned,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .size(200.dp, 150.dp)
        )
        
        // Detail panel (to the right of turntable)
        if (selectedVehicle != null) {
            VehicleDetailPanel(
                vehicleName = selectedVehicle.displayName ?: selectedVehicle.typeId,
                maxSpeed = "80 km/h", // In real app, fetch from catalog
                power = "6.2L Diesel",
                capacity = "48 pax",
                isOwned = isOwned,
                price = if (!isOwned) "KSH 850,000" else null,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 216.dp, top = 8.dp)
            )
        }
        
        // Theme toggle button (top-right)
        ThemeToggleButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        )
    }
}

@Composable
fun ThemeToggleButton(modifier: Modifier = Modifier) {
    var isLight by remember { mutableStateOf(false) }
    Button(
        onClick = {
            isLight = !isLight
            // Toggle theme via LocalTheme or system
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