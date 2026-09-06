package com.transportsim.app.ui.training

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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.transportsim.app.ui.training.components.*
import com.transportsim.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingScreen(
    onNavigateBack: () -> Unit,
    viewModel: TrainingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Purple)
                }
            }
            uiState.scenarios.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎯", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Training Scenarios Available",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Check back later for new training content.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Hero section
                    item {
                        TrainingHeroCard(
                            playerLevel = uiState.playerLevel,
                            completedCount = uiState.completedCount,
                            totalCount = uiState.totalCount
                        )
                    }

                    // Scenarios
                    items(uiState.scenarios) { scenarioWithProgress ->
                        TrainingScenarioCard(
                            scenario = scenarioWithProgress,
                            onStart = { viewModel.startTraining(scenarioWithProgress.scenario.id) },
                            onUnlock = { viewModel.unlockScenario(scenarioWithProgress.scenario.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Overall progress
                    item {
                        TrainingProgressCard(
                            completed = uiState.completedCount,
                            total = uiState.totalCount,
                            modifier = Modifier.fillMaxWidth()
                        )
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

    // "Not Yet Implemented" Dialog
    if (uiState.showNotImplementedDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissNotImplementedDialog() },
            title = { Text("🚧 Coming Soon", color = Gold) },
            text = {
                Column {
                    Text(
                        text = "Simulation is not yet implemented.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This training scenario will be available in a future update.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissNotImplementedDialog() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Cyan,
                        contentColor = Color.Black
                    )
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.medium
        )
    }
}
