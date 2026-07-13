package com.transportsim.app.ui.settings

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
import com.transportsim.app.ui.settings.components.*
import com.transportsim.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Teal
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Graphics & Display
            item {
                SettingsSection(
                    title = "Graphics & Display"
                ) {
                    SettingsRow(
                        label = "Render Quality",
                        description = "Affects 3D environment detail and draw distance",
                        control = {
                            SettingsDropdown(
                                options = listOf("Ultra", "High", "Medium", "Low"),
                                selected = uiState.renderQuality,
                                onSelected = { viewModel.setRenderQuality(it) }
                            )
                        }
                    )
                    SettingsRow(
                        label = "Shadow Quality",
                        description = "Real-time shadows on vehicles and buildings",
                        control = {
                            SettingsDropdown(
                                options = listOf("High", "Medium", "Off"),
                                selected = uiState.shadowQuality,
                                onSelected = { viewModel.setShadowQuality(it) }
                            )
                        }
                    )
                    SettingsRow(
                        label = "Anti-Aliasing",
                        description = "Smooth edges on 3D objects",
                        control = {
                            SettingsToggle(
                                checked = uiState.antiAliasing,
                                onCheckedChange = { viewModel.setAntiAliasing(it) }
                            )
                        }
                    )
                    SettingsRow(
                        label = "Traffic Density",
                        description = "Number of AI vehicles on road",
                        control = {
                            SettingsSlider(
                                value = uiState.trafficDensity,
                                onValueChange = { viewModel.setTrafficDensity(it) },
                                valueRange = 0f..100f,
                                steps = 10
                            )
                        }
                    )
                }
            }
            
            // Audio
            item {
                SettingsSection(
                    title = "Audio"
                ) {
                    SettingsRow(
                        label = "Master Volume",
                        description = "Overall game audio level",
                        control = {
                            SettingsSlider(
                                value = uiState.masterVolume,
                                onValueChange = { viewModel.setMasterVolume(it) },
                                valueRange = 0f..100f,
                                steps = 10
                            )
                        }
                    )
                    SettingsRow(
                        label = "Engine Sounds",
                        description = "Realistic vehicle engine audio",
                        control = {
                            SettingsToggle(
                                checked = uiState.engineSounds,
                                onCheckedChange = { viewModel.setEngineSounds(it) }
                            )
                        }
                    )
                    SettingsRow(
                        label = "Ambient Sounds",
                        description = "City noise, bird sounds, market sounds",
                        control = {
                            SettingsToggle(
                                checked = uiState.ambientSounds,
                                onCheckedChange = { viewModel.setAmbientSounds(it) }
                            )
                        }
                    )
                    SettingsRow(
                        label = "Radio / Music",
                        description = "In-game radio stations while driving",
                        control = {
                            SettingsToggle(
                                checked = uiState.radioMusic,
                                onCheckedChange = { viewModel.setRadioMusic(it) }
                            )
                        }
                    )
                }
            }
            
            // Gameplay
            item {
                SettingsSection(
                    title = "Gameplay"
                ) {
                    SettingsRow(
                        label = "Simulation Speed",
                        description = "Default time speed multiplier",
                        control = {
                            SettingsDropdown(
                                options = listOf("1×", "2×", "4×"),
                                selected = uiState.simulationSpeed,
                                onSelected = { viewModel.setSimulationSpeed(it) }
                            )
                        }
                    )
                    SettingsRow(
                        label = "Auto-Refuel",
                        description = "Automatically refuel when vehicles drop below 20%",
                        control = {
                            SettingsToggle(
                                checked = uiState.autoRefuel,
                                onCheckedChange = { viewModel.setAutoRefuel(it) }
                            )
                        }
                    )
                    SettingsRow(
                        label = "Show Tutorial Tips",
                        description = "Contextual hints during gameplay",
                        control = {
                            SettingsToggle(
                                checked = uiState.showTutorialTips,
                                onCheckedChange = { viewModel.setShowTutorialTips(it) }
                            )
                        }
                    )
                    SettingsRow(
                        label = "Language",
                        description = "Game interface language",
                        control = {
                            SettingsDropdown(
                                options = listOf("English", "Swahili", "Sheng"),
                                selected = uiState.language,
                                onSelected = { viewModel.setLanguage(it) }
                            )
                        }
                    )
                }
            }
            
            // Account
            item {
                SettingsSection(
                    title = "Account"
                ) {
                    SettingsRow(
                        label = "Player Name",
                        description = "Your display name on leaderboards",
                        control = {
                            SettingsTextField(
                                value = uiState.playerName,
                                onValueChange = { viewModel.setPlayerName(it) }
                            )
                        }
                    )
                    SettingsRow(
                        label = "Cloud Save",
                        description = "Sync progress across devices",
                        control = {
                            SettingsToggle(
                                checked = uiState.cloudSave,
                                onCheckedChange = { viewModel.setCloudSave(it) }
                            )
                        }
                    )
                    SettingsRow(
                        label = "Version",
                        description = "TransportSim v1.0.0 — Sphere Solution Developers",
                        control = {
                            Button(
                                onClick = { viewModel.checkUpdate() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text("Check Update", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    )
                }
            }
            
            // About / Credits
            item {
                SettingsSection(
                    title = "About"
                ) {
                    SettingsRow(
                        label = "TransportSim PVE",
                        description = "Real Map-Driven Transport Simulation",
                        control = {
                            Text(
                                text = "v1.0.0",
                                style = MaterialTheme.typography.labelSmall,
                                color = Teal
                            )
                        }
                    )
                    SettingsRow(
                        label = "Developed by",
                        description = "Sphere Solution Developers · Nairobi, Kenya",
                        control = {}
                    )
                }
            }
        }
    }
}