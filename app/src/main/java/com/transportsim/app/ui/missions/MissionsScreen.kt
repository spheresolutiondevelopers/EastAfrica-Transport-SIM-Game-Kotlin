package com.transportsim.app.ui.missions

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
import com.transportsim.app.ui.missions.components.*
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.MissionStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionsScreen(
    onMissionSelected: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: MissionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Active Missions",
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
                    // Filter by mission type
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
                            text = { Text("All Missions") },
                            onClick = {
                                viewModel.setFilter(null)
                                expanded = false
                            }
                        )
                        listOf("Cargo", "Passenger", "Express", "VIP", "Training").forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    viewModel.setFilter(type)
                                    expanded = false
                                }
                            )
                        }
                    }
                    // Timer display
                    Surface(
                        color = Cyan.copy(alpha = 0.15f),
                        shape = MaterialTheme.shapes.medium,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Cyan.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "⏰ ${uiState.resetTimer}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Cyan,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
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
                    CircularProgressIndicator(color = Gold)
                }
            }
            uiState.activeMissions.isEmpty() && uiState.availableMissions.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📋", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Missions Available",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Check back tomorrow for new missions.",
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
                    // Active missions section
                    if (uiState.activeMissions.isNotEmpty()) {
                        item {
                            Text(
                                text = "In Progress",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Gold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(uiState.activeMissions) { mission ->
                            MissionCard(
                                mission = mission,
                                isActive = true,
                                onAccept = { viewModel.acceptMission(mission.missionId) },
                                onComplete = { viewModel.completeMission(mission.missionId) },
                                onSelect = { onMissionSelected(mission.missionId) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    // Available missions section
                    if (uiState.availableMissions.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Available Missions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Cyan,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(uiState.availableMissions) { mission ->
                            MissionCard(
                                mission = mission,
                                isActive = false,
                                onAccept = { viewModel.acceptMission(mission.missionId) },
                                onComplete = {},
                                onSelect = { onMissionSelected(mission.missionId) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    // Reward track
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        RewardTrackCard(
                            currentStep = uiState.rewardStep,
                            totalSteps = uiState.totalRewardSteps,
                            rewards = uiState.rewards,
                            onClaim = { viewModel.claimReward() },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    // Leaderboard preview
                    item {
                        LeaderboardCard(
                            entries = uiState.leaderboard,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}