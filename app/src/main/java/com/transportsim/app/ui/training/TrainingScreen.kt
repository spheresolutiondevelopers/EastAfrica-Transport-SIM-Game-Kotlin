package com.transportsim.app.ui.training

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
import com.transportsim.app.ui.training.components.TrainingScenarioCard
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.TrainingScenario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingScreen(
    onStartTraining: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: TrainingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🎓",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "Training Center",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Purple
                        )
                    }
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
                    CircularProgressIndicator(color = Purple)
                }
            }
            uiState.scenarios.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Hero section
                    item {
                        TrainingHeroCard()
                    }
                    
                    // Scenarios
                    items(uiState.scenarios) { scenario ->
                        TrainingScenarioCard(
                            scenario = scenario,
                            isUnlocked = scenario.isUnlocked,
                            onStart = { onStartTraining(scenario.id) },
                            onUnlock = { viewModel.unlockScenario(scenario.id) },
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
    }
}