package com.transportsim.app.ui.fleet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FleetDetailScreen(
    vehicleId: Int,
    onNavigateBack: () -> Unit,
    onStartSimulation: (String, Int) -> Unit,
    onNavigateToGarage: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vehicle #$vehicleId") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Fleet Detail for Vehicle $vehicleId")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { onNavigateToGarage(vehicleId) }) {
                    Text("Go to Garage")
                }
            }
        }
    }
}
