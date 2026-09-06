package com.transportsim.app.ui.fleet

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Fleet Detail for Vehicle $vehicleId")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onNavigateToGarage(vehicleId) }) {
                Text("Go to Garage")
            }
        }
        
        // Back button in the top left
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.padding(16.dp).align(Alignment.TopStart)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
    }
}
