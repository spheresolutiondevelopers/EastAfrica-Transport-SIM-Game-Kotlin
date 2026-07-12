package com.transportsim.app.ui.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    init {
        loadDashboardData()
    }
    
    private fun loadDashboardData() {
        viewModelScope.launch {
            // Load player profile
            playerRepository.observeProfile()
                .combine(economyRepository.observeBalance()) { profile, balance ->
                    Triple(profile, balance, Unit)
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
            
            // Load fleet summary
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
                    _uiState.update { state ->
                        state.copy(
                            categories = categories,
                            vehicles = vehicles,
                            selectedVehicleId = vehicles.firstOrNull()?.vehicleId
                        )
                    }
                }
            
            // Load active missions
            missionRepository.observeMissions()
                .collect { missions ->
                    val activeMissions = missions.filter { it.status == MissionStatus.IN_PROGRESS }
                    _uiState.update { state ->
                        state.copy(activeMissions = activeMissions)
                    }
                }
            
            // Load route statuses
            routeRepository.observeRoutes()
                .collect { routes ->
                    val statuses = routes.map { route ->
                        RouteStatus(
                            routeId = route.routeId,
                            name = route.name,
                            status = if (route.isUnlocked) "LIVE" else "LOCKED"
                        )
                    }
                    _uiState.update { state ->
                        state.copy(routeStatuses = statuses)
                    }
                }
            
            // Load today's stats
            val todayStats = economyRepository.getDailyRewards()
            _uiState.update { state ->
                state.copy(
                    todayRevenue = 18240,
                    todayPassengers = 1847,
                    todayCargo = 24500,
                    todayOnTimeRate = 94.2f
                )
            }
            
            // Load fuel alerts
            val vehicles = fleetRepository.getOwnedVehicles()
            val alerts = vehicles
                .filter { it.fuelLevelPct < 40 }
                .map { FuelAlert(vehicleId = it.vehicleId, fuelPct = it.fuelLevelPct) }
            _uiState.update { state ->
                state.copy(fuelAlerts = alerts)
            }
            
            // Select first route
            val routes = routeRepository.getAllRoutes()
            if (routes.isNotEmpty()) {
                _uiState.update { state ->
                    state.copy(selectedRouteId = routes.first().routeId)
                }
            }
        }
    }
}

data class DashboardUiState(
    val categories: List<CategoryProgress> = emptyList(),
    val vehicles: List<Vehicle> = emptyList(),
    val activeMissions: List<Mission> = emptyList(),
    val routeStatuses: List<RouteStatus> = emptyList(),
    val fuelAlerts: List<FuelAlert> = emptyList(),
    val playerLevel: Int = 1,
    val playerXp: Int = 0,
    val nextLevelXp: Int = 1000,
    val playerBalance: Int = 0,
    val todayRevenue: Int = 0,
    val todayPassengers: Int = 0,
    val todayCargo: Int = 0,
    val todayOnTimeRate: Float = 0f,
    val selectedRouteId: String? = null,
    val selectedVehicleId: Int? = null
)

data class CategoryProgress(
    val category: VehicleCategory,
    val owned: Int,
    val total: Int
) {
    val progress: Float = if (total > 0) owned.toFloat() / total else 0f
}

data class RouteStatus(
    val routeId: String,
    val name: String,
    val status: String
)

data class FuelAlert(
    val vehicleId: Int,
    val fuelPct: Float
)