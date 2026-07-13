package com.transportsim.app.ui.routes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transportsim.domain.models.Route
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
        loadRoutes()
    }
    
    private fun loadRoutes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Get all routes
            val allRoutes = routeRepository.getAllRoutes()
            val unlockedRoutes = routeRepository.getUnlockableRoutes()
            
            // Filter by terrain
            val filtered = if (terrainFilter != null) {
                allRoutes.filter { it.terrainType.name == terrainFilter }
            } else {
                allRoutes
            }
            
            // Separate unlocked and locked
            val unlockedIds = unlockedRoutes.map { it.routeId }.toSet()
            val available = filtered.filter { it.routeId in unlockedIds }
            val locked = filtered.filter { it.routeId !in unlockedIds }
            
            _uiState.update { state ->
                state.copy(
                    routes = available,
                    lockedRoutes = locked,
                    isLoading = false
                )
            }
        }
    }
    
    fun setTerrainFilter(terrain: String?) {
        terrainFilter = terrain
        loadRoutes()
    }
    
    fun selectRoute(routeId: String) {
        _uiState.update { state ->
            state.copy(selectedRouteId = routeId)
        }
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
                    // In a real app, we would use an AssignVehicleToRouteUseCase
                    // For now, just update the vehicle's route
                    val updatedVehicle = availableVehicle.copy(currentRouteId = routeId)
                    playerRepository.updateVehicle(updatedVehicle)
                    
                    // Refresh routes
                    loadRoutes()
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
                loadRoutes()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

data class RoutesUiState(
    val routes: List<Route> = emptyList(),
    val lockedRoutes: List<Route> = emptyList(),
    val selectedRouteId: String? = null,
    val isLoading: Boolean = false
)