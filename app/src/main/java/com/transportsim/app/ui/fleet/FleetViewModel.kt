package com.transportsim.app.ui.fleet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transportsim.domain.models.FleetVehicle
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

    fun loadFleet() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Get full fleet (catalog + ownership)
                val allVehicles = fleetRepository.getFullFleet()

                if (allVehicles.isEmpty()) {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = "Catalog is empty. Check assets/config/vehicle_specs.json"
                    ) }
                    return@launch
                }

                // Apply filter
                val filtered = if (filterCategory != null) {
                    allVehicles.filter { it.catalogEntry.category == filterCategory }
                } else {
                    allVehicles
                }

                // Apply sort (by display name)
                val sorted = if (sortAscending) {
                    filtered.sortedBy { it.catalogEntry.displayName }
                } else {
                    filtered.sortedByDescending { it.catalogEntry.displayName }
                }

                _uiState.update { state ->
                    state.copy(
                        fleetVehicles = sorted,
                        isLoading = false,
                        sortAscending = sortAscending,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = "Failed to load fleet: ${e.message}"
                ) }
            }
        }
    }

    fun setFilter(category: VehicleCategory?) {
        filterCategory = category
        loadFleet()
    }

    fun toggleSortOrder() {
        sortAscending = !sortAscending
        loadFleet()
    }

    fun purchaseVehicle(typeId: String) {
        viewModelScope.launch {
            try {
                fleetRepository.purchaseVehicle(typeId)
                loadFleet() // Refresh
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deployVehicle(vehicleId: Int) {
        viewModelScope.launch {
            try {
                playerRepository.updateVehicleStatus(vehicleId, VehicleStatus.ACTIVE)
                loadFleet()
            } catch (e: Exception) {
            }
        }
    }

    fun serviceVehicle(vehicleId: Int) {
        viewModelScope.launch {
            try {
                val vehicle = playerRepository.getPlayerVehicle(vehicleId)
                val updatedVehicle = vehicle.copy(
                    conditionPct = 100f,
                    engineHealthPct = 100f,
                    tyreConditionPct = 100f,
                    lastMaintainedAt = System.currentTimeMillis().toString()
                )
                playerRepository.updateVehicle(updatedVehicle)
                loadFleet()
            } catch (e: Exception) {
            }
        }
    }
}

data class FleetUiState(
    val fleetVehicles: List<FleetVehicle> = emptyList(),
    val isLoading: Boolean = false,
    val sortAscending: Boolean = true,
    val error: String? = null
)
