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

    fun selectCategory(category: VehicleCategory) {
        viewModelScope.launch {
            val vehicles = fleetRepository.getVehiclesByCategory(category)
            _uiState.update { it.copy(
                categoryVehicles = vehicles,
                selectedTurntableIndex = 0
            ) }
        }
    }

    fun nextTurntableVehicle() {
        _uiState.update { state ->
            if (state.categoryVehicles.isEmpty()) return@update state
            val nextIndex = (state.selectedTurntableIndex + 1) % state.categoryVehicles.size
            state.copy(selectedTurntableIndex = nextIndex)
        }
    }

    fun previousTurntableVehicle() {
        _uiState.update { state ->
            if (state.categoryVehicles.isEmpty()) return@update state
            val prevIndex = if (state.selectedTurntableIndex > 0) 
                state.selectedTurntableIndex - 1 
            else 
                state.categoryVehicles.size - 1
            state.copy(selectedTurntableIndex = prevIndex)
        }
    }

    /**
     * Maps vehicle type ID to its 3D asset path.
     */
    fun getAssetPathForVehicle(typeId: String?): String? {
        if (typeId == null) return null
        
        // Example mapping based on vehicle type ID prefix
        return when {
            typeId.contains("taxi_estate") -> "3d/vehicles/taxi_estate.glb"
            typeId.contains("bus_city") -> "3d/vehicles/bus_city.glb"
            typeId.contains("bus_coach") -> "3d/vehicles/bus_coach.glb"
            typeId.contains("matatu") -> "3d/vehicles/matatu_classic.glb"
            typeId.contains("pickup") -> "3d/vehicles/pickup_single_cab.glb"
            // Fallback to existing model to prevent FileNotFoundException in Logcat
            else -> "3d/vehicles/taxi_estate.glb"
        }
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
                            } ?: VehicleCategory.PICKUP
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
                    // Initial category selection
                    if (_uiState.value.categoryVehicles.isEmpty()) {
                        selectCategory(VehicleCategory.PICKUP)
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
            economyRepository.observeTodayPerformance()
                .collect { performance ->
                    _uiState.update { state ->
                        state.copy(
                            todayRevenue = performance.revenueKsh,
                            todayPassengers = performance.passengers,
                            todayCargo = performance.cargoKg,
                            todayOnTimeRate = performance.onTimeRatePct
                        )
                    }
                }
        }

        viewModelScope.launch {
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
