package com.transportsim.app.ui.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transportsim.app.audio.AudioManager
import com.transportsim.app.ui.dashboard.models.*
import com.transportsim.domain.models.*
import com.transportsim.domain.repositories.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val fleetRepository: FleetRepository,
    private val routeRepository: RouteRepository,
    private val missionRepository: MissionRepository,
    private val economyRepository: EconomyRepository,
    private val audioManager: AudioManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    val isMuted: StateFlow<Boolean> = audioManager.isMuted
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    init {
        loadDashboardData()
    }
    
    fun toggleMute() {
        audioManager.toggleMute()
    }

    private fun loadDashboardData() {
        // Observe player profile and balance
        viewModelScope.launch {
            playerRepository.observeProfile()
                .combine(economyRepository.observeBalance()) { profile, balance ->
                    profile to balance
                }
                .collect { (profile, balance) ->
                    _uiState.update { state ->
                        state.copy(
                            playerLevel = profile.level,
                            playerXp = profile.xp,
                            nextLevelXp = (profile.level + 1) * 1000,
                            playerBalance = balance
                        )
                    }
                }
        }

        // Observe fleet summary and update mapVehicles
        viewModelScope.launch {
            fleetRepository.observeFleet()
                .collect { vehicles ->
                    val categories = VehicleCategory.values().map { category ->
                        val owned = vehicles.filter { it.typeId.startsWith(category.name.lowercase()) }.size
                        val total = 5 // Each category has 5 variants
                        CategoryProgress(
                            category = category,
                            owned = owned,
                            total = total
                        )
                    }
                    val mapVehicles = vehicles.map { vehicle ->
                        MapVehicle(
                            id = vehicle.vehicleId.toString(),
                            x = (200..800).random().toFloat(),
                            y = (100..500).random().toFloat(),
                            category = VehicleCategory.values().find { 
                                vehicle.typeId.startsWith(it.name.lowercase()) 
                            } ?: VehicleCategory.BUS
                        )
                    }
                    _uiState.update { state ->
                        state.copy(
                            categories = categories,
                            vehicles = vehicles,
                            mapVehicles = mapVehicles,
                            selectedVehicleId = vehicles.firstOrNull()?.vehicleId
                        )
                    }
                }
        }
        
        // Observe active missions
        viewModelScope.launch {
            missionRepository.observeMissions()
                .collect { missions ->
                    val activeMissions = missions.filter { it.status == MissionStatus.IN_PROGRESS }
                    _uiState.update { state ->
                        state.copy(activeMissions = activeMissions)
                    }
                }
        }
        
        // Observe route statuses combined with player level
        viewModelScope.launch {
            routeRepository.observeRoutes()
                .combine(playerRepository.observeProfile()) { routes, profile ->
                    routes to profile.level
                }
                .collect { (routes, playerLevel) ->
                    val statuses = routes.map { route ->
                        RouteStatus(
                            routeId = route.id,
                            name = route.name,
                            status = if (route.unlockLevel <= playerLevel) "LIVE" else "LOCKED"
                        )
                    }
                    _uiState.update { state ->
                        state.copy(routeStatuses = statuses)
                    }
                }
        }
        
        // Load today's stats and alerts
        viewModelScope.launch {
            val todayStats = economyRepository.getDailyRewards()
            _uiState.update { state ->
                state.copy(
                    todayRevenue = 18240,
                    todayPassengers = 1847,
                    todayCargo = 24500,
                    todayOnTimeRate = 94.2f
                )
            }
            
            val vehicles = fleetRepository.getOwnedVehicles()
            val alerts = vehicles
                .filter { it.fuelLevelPct < 40 }
                .map { FuelAlert(vehicleId = it.vehicleId, fuelPct = it.fuelLevelPct) }
            _uiState.update { state ->
                state.copy(fuelAlerts = alerts)
            }
            
            val routes = routeRepository.getAllRoutes()
            if (routes.isNotEmpty() && _uiState.value.selectedRouteId == null) {
                _uiState.update { state ->
                    state.copy(selectedRouteId = routes.first().id)
                }
            }
        }
    }
}
