package com.transportsim.app.ui.garage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.transportsim.app.ui.garage.components.*
import com.transportsim.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarageScreen(
    onVehicleSelected: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: GarageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Orange)
                }
            }
            uiState.vehicles.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔧", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No Vehicles in Garage", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Purchase vehicles from the Fleet screen.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            else -> {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Left: Vehicle list
                    GarageVehicleList(
                        vehicles = uiState.vehicles,
                        selectedVehicleId = uiState.selectedVehicleId,
                        onVehicleSelected = { viewModel.selectVehicle(it) },
                        modifier = Modifier.width(200.dp).fillMaxHeight()
                    )

                    // Center: Main content
                    Column(modifier = Modifier.weight(1f).fillMaxHeight().padding(16.dp)) {
                        // Showcase
                        GarageShowcase(
                            vehicle = uiState.selectedVehicle,
                            spec = uiState.selectedVehicleSpec,
                            onEditLivery = viewModel::editLivery,
                            onFullService = viewModel::fullService,
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
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.upgrades) { upgrade ->
                                UpgradeCard(
                                    upgrade = upgrade,
                                    onUpgrade = { viewModel.upgradeModule(upgrade.definition.upgradeId) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    // Right: Inventory
                    GarageInventoryPanel(
                        inventory = uiState.inventory,
                        liveries = uiState.liveries,
                        modifier = Modifier.width(220.dp).fillMaxHeight().padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
