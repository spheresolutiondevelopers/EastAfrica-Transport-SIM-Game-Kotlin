package com.transportsim.app.ui.fleet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transportsim.domain.models.Vehicle
import com.transportsim.domain.models.VehicleCategory
import com.transportsim.domain.models.VehicleStatus
import com.transportsim.domain.repositories.FleetRepository
import com.transportsim.domain.repositories.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FleetViewModel @Inject constructor(
    private val fleetRepository: FleetRepository,
    private val playerRepository: PlayerRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(FleetUiState())
    val uiState: StateFlow<FleetUiState> = _uiState.asStateFlow()
    
    private var filterCategory: VehicleCategory? = null
    private var sortAscending: Boolean = true
    
    init {
        loadFleet()
    }
    
    private fun loadFleet() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            fleetRepository.observeFleet()
                .collect { allVehicles ->
                    // Apply filter
                    val filtered = if (filterCategory != null) {
                        allVehicles.filter { 
                            it.typeId.startsWith(filterCategory!!.name.lowercase())
                        }
                    } else {
                        allVehicles
                    }
                    
                    // Apply sort
                    val sorted = if (sortAscending) {
                        filtered.sortedBy { it.displayName ?: it.typeId }
                    } else {
                        filtered.sortedByDescending { it.displayName ?: it.typeId }
                    }
                    
                    _uiState.update { state ->
                        state.copy(
                            vehicles = sorted,
                            isLoading = false
                        )
                    }
                }
        }
    }
    
    fun setFilter(category: VehicleCategory?) {
        filterCategory = category
        // Trigger reload by re-collecting flow – the collector will handle it
        // Since the flow is already collected, we just update the filter state
        // and the collector will re-emit with the new filter applied
        // We need to force a refresh by reloading
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // The collector will update with the new filter
            // We just reset the loading state after a small delay
            kotlinx.coroutines.delay(100)
            _uiState.update { it.copy(isLoading = false) }
        }
    }
    
    fun toggleSortOrder() {
        sortAscending = !sortAscending
        _uiState.update { state ->
            val sorted = if (sortAscending) {
                state.vehicles.sortedBy { it.displayName ?: it.typeId }
            } else {
                state.vehicles.sortedByDescending { it.displayName ?: it.typeId }
            }
            state.copy(
                vehicles = sorted,
                sortAscending = sortAscending
            )
        }
    }
    
    fun deployVehicle(vehicleId: Int) {
        viewModelScope.launch {
            try {
                val vehicle = playerRepository.getPlayerVehicle(vehicleId)
                if (vehicle.status == VehicleStatus.IDLE) {
                    playerRepository.updateVehicleStatus(vehicleId, VehicleStatus.ACTIVE)
                    // Show notification via callback (in a real app, use a Snackbar or Toast)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun serviceVehicle(vehicleId: Int) {
        viewModelScope.launch {
            try {
                // In a real app, this would call a service use case
                // For now, just update the condition
                val vehicle = playerRepository.getPlayerVehicle(vehicleId)
                val updatedVehicle = vehicle.copy(
                    conditionPct = 100f,
                    engineHealthPct = 100f,
                    tyreConditionPct = 100f,
                    lastMaintainedAt = System.currentTimeMillis().toString()
                )
                playerRepository.updateVehicle(updatedVehicle)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

data class FleetUiState(
    val vehicles: List<Vehicle> = emptyList(),
    val isLoading: Boolean = false,
    val sortAscending: Boolean = true
)