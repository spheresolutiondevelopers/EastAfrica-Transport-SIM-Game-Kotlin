package com.transportsim.app.ui.fleet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.transportsim.app.ui.fleet.components.FleetCategoryHeader
import com.transportsim.app.ui.fleet.components.FleetVehicleCard
import com.transportsim.app.ui.theme.Cyan
import com.transportsim.app.ui.theme.Gold
import com.transportsim.domain.models.VehicleCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FleetScreen(
    onVehicleSelected: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: FleetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fleet Manager", color = Gold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Filter dropdown
                    var filterExpanded by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { filterExpanded = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter")
                        }
                        DropdownMenu(
                            expanded = filterExpanded,
                            onDismissRequest = { filterExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All") },
                                onClick = {
                                    viewModel.setFilter(null)
                                    filterExpanded = false
                                }
                            )
                            VehicleCategory.values().forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        viewModel.setFilter(category)
                                        filterExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    // Sort toggle
                    IconButton(onClick = { viewModel.toggleSortOrder() }) {
                        Icon(
                            imageVector = if (uiState.sortAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = "Sort"
                        )
                    }
                    // Settings
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Cyan)
            }
            
            if (uiState.error != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Error Loading Fleet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = uiState.error ?: "Unknown error",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Button(
                            onClick = { viewModel.loadFleet() },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            if (uiState.fleetVehicles.isEmpty() && !uiState.isLoading && uiState.error == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No vehicles found in catalog.", style = MaterialTheme.typography.bodyLarge)
                }
            } else if (uiState.fleetVehicles.isNotEmpty()) {
                val groupedVehicles = uiState.fleetVehicles.groupBy { it.catalogEntry.category }
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    groupedVehicles.forEach { (category, vehicles) ->
                        item {
                            FleetCategoryHeader(
                                category = category.name,
                                count = vehicles.size,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        val chunks = vehicles.chunked(2)
                        items(chunks) { rowVehicles ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowVehicles.forEach { fleetVehicle ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        FleetVehicleCard(
                                            fleetVehicle = fleetVehicle,
                                            onPurchase = { viewModel.purchaseVehicle(it) },
                                            onDeploy = { fleetVehicle.vehicleId?.let { viewModel.deployVehicle(it) } },
                                            onService = { fleetVehicle.vehicleId?.let { viewModel.serviceVehicle(it) } },
                                            onSelect = { onVehicleSelected(fleetVehicle.vehicleId ?: 0) }
                                        )
                                    }
                                }
                                if (rowVehicles.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
