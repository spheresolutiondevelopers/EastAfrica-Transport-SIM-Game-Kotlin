package com.transportsim.app.ui.fleet

import android.util.Log
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
    private var selectedIndex: Int = 0

    init {
        loadFleetData()
    }

    fun loadFleetData() {
        Log.d("FleetViewModel", "loadFleetData: Starting fetch...")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Get full fleet (catalog + ownership)
                val allVehicles = fleetRepository.getFullFleet()
                Log.d("FleetViewModel", "loadFleetData: Fetched ${allVehicles.size} total vehicles")

                if (allVehicles.isEmpty()) {
                    Log.w("FleetViewModel", "loadFleetData: Catalog is empty")
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = "Catalog is empty."
                    ) }
                    return@launch
                }

                // Apply filter
                val filtered = if (filterCategory != null) {
                    allVehicles.filter { it.catalogEntry.category == filterCategory }
                } else {
                    allVehicles
                }
                Log.d("FleetViewModel", "loadFleetData: Filtered to ${filtered.size} vehicles for $filterCategory")

                // Ensure selected index is valid
                if (selectedIndex >= filtered.size) {
                    selectedIndex = if (filtered.isNotEmpty()) 0 else 0
                }

                // Get categories with counts
                val categories = VehicleCategory.values().map { category ->
                    val count = allVehicles.count { it.catalogEntry.category == category }
                    FleetCategory(category, count)
                }

                // Get available (not owned) vehicles for purchase modal
                val available = allVehicles.filter { !it.isOwned }

                _uiState.update { state ->
                    state.copy(
                        fleetVehicles = filtered,
                        selectedIndex = selectedIndex,
                        categories = categories,
                        availableVehicles = available,
                        selectedCategory = filterCategory,
                        isLoading = false,
                        error = null
                    )
                }
                Log.d("FleetViewModel", "loadFleetData: UI State updated successfully")
            } catch (e: Exception) {
                Log.e("FleetViewModel", "loadFleetData: Error", e)
                _uiState.update { it.copy(
                    isLoading = false,
                    error = "Failed to load fleet: ${e.message}"
                ) }
            }
        }
    }

    fun setFilter(category: VehicleCategory?) {
        filterCategory = category
        selectedIndex = 0
        loadFleetData()
    }

    fun selectVehicle(index: Int) {
        selectedIndex = index
        _uiState.update { state ->
            state.copy(selectedIndex = index)
        }
    }

    fun nextVehicle() {
        val size = _uiState.value.fleetVehicles.size
        if (size > 1) {
            selectedIndex = (selectedIndex + 1) % size
            _uiState.update { state ->
                state.copy(selectedIndex = selectedIndex)
            }
        }
    }

    fun previousVehicle() {
        val size = _uiState.value.fleetVehicles.size
        if (size > 1) {
            selectedIndex = if (selectedIndex - 1 < 0) size - 1 else selectedIndex - 1
            _uiState.update { state ->
                state.copy(selectedIndex = selectedIndex)
            }
        }
    }

    fun toggleViewMode() {
        _uiState.update { state ->
            state.copy(isInteriorMode = !state.isInteriorMode)
        }
    }

    fun showPurchaseModal() {
        _uiState.update { state ->
            state.copy(showPurchaseModal = true)
        }
    }

    fun dismissPurchaseModal() {
        _uiState.update { state ->
            state.copy(showPurchaseModal = false)
        }
    }

    fun purchaseVehicle(typeId: String) {
        viewModelScope.launch {
            try {
                val result = fleetRepository.purchaseVehicle(typeId)
                if (result.isSuccess) {
                    dismissPurchaseModal()
                    loadFleetData()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun serviceVehicle(vehicleId: Int) {
        viewModelScope.launch {
            try {
                playerRepository.fullServiceVehicle(vehicleId)
                loadFleetData()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun upgradeVehicle(vehicleId: Int) {
        // Navigate to garage with this vehicle
    }

    fun customizeVehicle(vehicleId: Int) {
        // Navigate to livery editor
    }
}

data class FleetUiState(
    val fleetVehicles: List<FleetVehicle> = emptyList(),
    val selectedIndex: Int = 0,
    val categories: List<FleetCategory> = emptyList(),
    val availableVehicles: List<FleetVehicle> = emptyList(),
    val selectedCategory: VehicleCategory? = null,
    val isLoading: Boolean = false,
    val isInteriorMode: Boolean = false,
    val showPurchaseModal: Boolean = false,
    val error: String? = null
)

data class FleetCategory(
    val category: VehicleCategory,
    val count: Int
)
