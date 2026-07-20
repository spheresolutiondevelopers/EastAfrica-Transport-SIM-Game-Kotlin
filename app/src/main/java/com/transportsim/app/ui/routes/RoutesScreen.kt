package com.transportsim.app.ui.routes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.transportsim.app.ui.routes.components.RouteCard
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutesScreen(
    onRouteSelected: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: RoutesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kenya Routes",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Green
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Filter by terrain type
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Routes") },
                            onClick = {
                                viewModel.setTerrainFilter(null)
                                expanded = false
                            }
                        )
                        listOf("Urban", "Highway", "Expressway", "Rural", "Coastal").forEach { terrain ->
                            DropdownMenuItem(
                                text = { Text(terrain) },
                                onClick = {
                                    viewModel.setTerrainFilter(terrain)
                                    expanded = false
                                }
                            )
                        }
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
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
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Green)
                }
            }
            uiState.routes.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📍", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Routes Available",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Check back later for new routes.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Show training routes first
                    val trainingRoutes = uiState.routes.filter { it.isTraining }
                    val regularRoutes = uiState.routes.filter { !it.isTraining }

                    if (trainingRoutes.isNotEmpty()) {
                        item {
                            Text(
                                text = "🎓 Training",
                                style = MaterialTheme.typography.titleMedium,
                                color = Purple,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(trainingRoutes) { route ->
                            RouteCard(
                                route = route,
                                stats = uiState.routeStatsMap[route.id],
                                isSelected = uiState.selectedRouteId == route.id,
                                isLocked = route.unlockLevel > uiState.playerLevel,
                                onClick = { 
                                    viewModel.selectRoute(route.id)
                                    onRouteSelected(route.id)
                                },
                                onAssign = { viewModel.assignVehicleToRoute(route.id) },
                                onUnlock = { viewModel.unlockRoute(route.id) },
                                onStart = { viewModel.startRoute(route.id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    if (regularRoutes.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "📍 Kenya Routes",
                                style = MaterialTheme.typography.titleMedium,
                                color = Green,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(regularRoutes) { route ->
                            RouteCard(
                                route = route,
                                stats = uiState.routeStatsMap[route.id],
                                isSelected = uiState.selectedRouteId == route.id,
                                isLocked = route.unlockLevel > uiState.playerLevel,
                                onClick = { 
                                    viewModel.selectRoute(route.id)
                                    onRouteSelected(route.id)
                                },
                                onAssign = { viewModel.assignVehicleToRoute(route.id) },
                                onUnlock = { viewModel.unlockRoute(route.id) },
                                onStart = { viewModel.startRoute(route.id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}