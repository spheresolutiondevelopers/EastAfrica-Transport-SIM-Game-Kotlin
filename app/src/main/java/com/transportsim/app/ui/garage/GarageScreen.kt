package com.transportsim.app.ui.garage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.transportsim.app.ui.garage.components.*
import com.transportsim.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarageScreen(
    onVehicleSelected: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: GarageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Garage",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Orange
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
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
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Orange)
                }
            }
            uiState.vehicles.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔧", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Vehicles in Garage",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Purchase vehicles from the Fleet screen first.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Left: Vehicle list
                    GarageVehicleList(
                        vehicles = uiState.vehicles,
                        selectedVehicleId = uiState.selectedVehicleId,
                        onVehicleSelected = { vehicleId ->
                            viewModel.selectVehicle(vehicleId)
                            onVehicleSelected(vehicleId)
                        },
                        modifier = Modifier
                            .width(200.dp)
                            .fillMaxHeight()
                    )
                    
                    // Center: Main content
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(16.dp)
                    ) {
                        // Showcase card
                        GarageShowcase(
                            vehicle = uiState.selectedVehicle,
                            upgrades = uiState.upgrades,
                            onEditLivery = { viewModel.editLivery() },
                            onFullService = { viewModel.fullService() },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        // Upgrade modules
                        Text(
                            text = "Upgrade Modules",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                        
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.upgrades) { upgrade ->
                                UpgradeCard(
                                    upgrade = upgrade,
                                    onUpgrade = { viewModel.upgradeModule(upgrade.upgradeId) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                    
                    // Right: Inventory panel
                    GarageInventoryPanel(
                        inventory = uiState.inventory,
                        liveries = uiState.liveries,
                        modifier = Modifier
                            .width(220.dp)
                            .fillMaxHeight()
                            .padding(start = 8.dp)
                    )
                }
            }
        }
    }
}