package com.transportsim.app.ui.fleet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.transportsim.app.ui.fleet.components.*
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.VehicleCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FleetScreen(
    onVehicleSelected: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: FleetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Fleet Manager",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Gold
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
                actions = {
                    // Filter dropdown
                    Box(modifier = Modifier.padding(end = 8.dp)) {
                        var expanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.FilterList,
                                contentDescription = "Filter"
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Categories") },
                                onClick = {
                                    viewModel.setFilter(null)
                                    expanded = false
                                }
                            )
                            VehicleCategory.values().forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        viewModel.setFilter(category)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    // Sort button
                    IconButton(onClick = { viewModel.toggleSortOrder() }) {
                        Icon(
                            imageVector = if (uiState.sortAscending)
                                androidx.compose.material.icons.Icons.Default.ArrowUpward
                            else
                                androidx.compose.material.icons.Icons.Default.ArrowDownward,
                            contentDescription = "Sort"
                        )
                    }
                    // Purchase button
                    Button(
                        onClick = { /* Navigate to vehicle catalog */ },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Cyan,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("+ Purchase", fontWeight = FontWeight.Bold)
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
                    CircularProgressIndicator(color = Cyan)
                }
            }
            uiState.vehicles.isEmpty() -> {
                FleetEmptyState(
                    onPurchaseClick = { /* Navigate to catalog */ },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            else -> {
                // Group vehicles by category
                val groupedVehicles = uiState.vehicles.groupBy { 
                    it.typeId.split("_").firstOrNull()?.uppercase() ?: "OTHER"
                }
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    groupedVehicles.forEach { (category, vehicles) ->
                        // Category header
                        item {
                            FleetCategoryHeader(
                                category = category,
                                count = vehicles.size,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        // Vehicles in this category (using staggered grid for visual variety)
                        item {
                            LazyVerticalStaggeredGrid(
                                columns = StaggeredGridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalItemSpacing = 12.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(vehicles) { vehicle ->
                                    FleetVehicleCard(
                                        vehicle = vehicle,
                                        onClick = { onVehicleSelected(vehicle.vehicleId) },
                                        onDeploy = { viewModel.deployVehicle(vehicle.vehicleId) },
                                        onService = { viewModel.serviceVehicle(vehicle.vehicleId) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}