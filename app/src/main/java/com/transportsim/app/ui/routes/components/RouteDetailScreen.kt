package com.transportsim.app.ui.routes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.transportsim.app.ui.routes.components.RouteInfoCard
import com.transportsim.app.ui.routes.components.RouteMapPreview
import com.transportsim.app.ui.routes.components.TodayStatsCard
import com.transportsim.app.ui.routes.components.WaypointList
import com.transportsim.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailScreen(
    routeId: String,
    onNavigateBack: () -> Unit,
    onStartSimulation: (String, Int) -> Unit,
    viewModel: RouteDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Load route details when ID changes
    LaunchedEffect(routeId) {
        viewModel.loadRouteDetail(routeId)
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Cyan)
            }
        } else if (uiState.route == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Route not found", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Map preview
                item {
                    RouteMapPreview(
                        route = uiState.route!!,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
                
                // Route info cards
                item {
                    RouteInfoCard(route = uiState.route!!)
                }
                
                // Waypoints
                item {
                    WaypointList(
                        waypoints = uiState.waypoints,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                // Today's stats
                item {
                    TodayStatsCard(routeId = routeId)
                }

                // Start button at the bottom of the list or as a sticky footer?
                // Let's put it at the bottom for now.
                item {
                    Button(
                        onClick = { onStartSimulation(routeId, 1) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Green,
                            contentColor = Color.White
                        )
                    ) {
                        Text("▶ Start Simulation", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Back button in the top left
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.padding(16.dp).align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
    }
}
