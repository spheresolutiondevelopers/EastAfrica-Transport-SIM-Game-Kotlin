package com.transportsim.app.ui.routes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transportsim.domain.models.Route
import com.transportsim.domain.models.RouteStats
import com.transportsim.domain.repositories.PlayerRepository
import com.transportsim.domain.repositories.RouteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutesViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val playerRepository: PlayerRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RoutesUiState())
    val uiState: StateFlow<RoutesUiState> = _uiState.asStateFlow()
    
    private var terrainFilter: String? = null
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Get all routes and player level
            val allRoutes = routeRepository.getAllRoutes()
            val playerProfile = playerRepository.getProfile()
            val playerLevel = playerProfile.level
            
            // Load all route stats
            val allStats = routeRepository.getAllRouteStats()
            val statsMap = allStats.associateBy { it.routeId }
            
            // Filter by terrain
            val filtered = if (terrainFilter != null) {
                allRoutes.filter { it.terrainType.name.equals(terrainFilter, ignoreCase = true) }
            } else {
                allRoutes
            }
            
            _uiState.update { state ->
                state.copy(
                    routes = filtered,
                    routeStatsMap = statsMap,
                    playerLevel = playerLevel,
                    isLoading = false
                )
            }
        }
    }
    
    fun setTerrainFilter(terrain: String?) {
        terrainFilter = terrain
        loadData()
    }
    
    fun selectRoute(routeId: String) {
        _uiState.update { state ->
            state.copy(selectedRouteId = routeId)
        }
    }

    fun startRoute(routeId: String) {
        // This would typically navigate to the driving screen or start a session
        // For now, it's a placeholder
    }
    
    fun assignVehicleToRoute(routeId: String) {
        viewModelScope.launch {
            try {
                // Get a suitable vehicle (simplified)
                val vehicles = playerRepository.getPlayerVehicles()
                val availableVehicle = vehicles.firstOrNull { 
                    it.currentRouteId == null || it.currentRouteId?.isEmpty() == true
                }
                
                if (availableVehicle != null) {
                    val updatedVehicle = availableVehicle.copy(currentRouteId = routeId)
                    playerRepository.updateVehicle(updatedVehicle)
                    loadData()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun unlockRoute(routeId: String) {
        viewModelScope.launch {
            try {
                routeRepository.unlockRoute(routeId)
                loadData()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

data class RoutesUiState(
    val routes: List<Route> = emptyList(),
    val routeStatsMap: Map<String, RouteStats> = emptyMap(),
    val selectedRouteId: String? = null,
    val playerLevel: Int = 1,
    val isLoading: Boolean = false
)