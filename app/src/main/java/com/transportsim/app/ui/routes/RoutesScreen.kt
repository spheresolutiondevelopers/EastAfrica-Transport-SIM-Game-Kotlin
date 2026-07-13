package com.transportsim.app.ui.routes

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
import com.transportsim.app.ui.routes.components.RouteCard
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutesScreen(
    onRouteSelected: (String) -> Unit,
    onNavigateBack: () -> Unit,
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
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Filter by terrain type
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
                    items(uiState.routes) { route ->
                        RouteCard(
                            route = route,
                            isSelected = uiState.selectedRouteId == route.routeId,
                            onClick = { 
                                viewModel.selectRoute(route.routeId)
                                onRouteSelected(route.routeId)
                            },
                            onAssign = { viewModel.assignVehicleToRoute(route.routeId) },
                            onUnlock = { viewModel.unlockRoute(route.routeId) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    // DLC / locked route section
                    if (uiState.lockedRoutes.isNotEmpty()) {
                        item {
                            Divider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                            Text(
                                text = "🔒 Locked Routes",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(uiState.lockedRoutes) { route ->
                            RouteCard(
                                route = route,
                                isSelected = false,
                                isLocked = true,
                                onClick = { /* Show unlock dialog */ },
                                onAssign = {},
                                onUnlock = { viewModel.unlockRoute(route.routeId) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}