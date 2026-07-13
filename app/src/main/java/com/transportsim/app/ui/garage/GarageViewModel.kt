package com.transportsim.app.ui.garage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transportsim.domain.models.*
import com.transportsim.domain.repositories.CatalogRepository
import com.transportsim.domain.repositories.EconomyRepository
import com.transportsim.domain.repositories.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GarageViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val catalogRepository: CatalogRepository,
    private val economyRepository: EconomyRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GarageUiState())
    val uiState: StateFlow<GarageUiState> = _uiState.asStateFlow()
    
    private var selectedVehicleId: Int? = null
    
    init {
        loadGarageData()
    }
    
    private fun loadGarageData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Get all vehicles
            val vehicles = playerRepository.getPlayerVehicles()
            val ownedVehicles = vehicles.filter { it.status != VehicleStatus.GARAGE }
            
            // Select first vehicle if none selected
            if (selectedVehicleId == null && ownedVehicles.isNotEmpty()) {
                selectedVehicleId = ownedVehicles.first().vehicleId
            }
            
            // Get upgrades for selected vehicle
            val upgrades = if (selectedVehicleId != null) {
                getUpgradesForVehicle(selectedVehicleId!!)
            } else {
                emptyList()
            }
            
            // Get inventory
            val inventory = listOf(
                InventoryItem("Engine Parts", "🔩", 3),
                InventoryItem("Tyres", "🛞", 8),
                InventoryItem("Battery", "🔋", 2),
                InventoryItem("Oil Filter", "🛢", 0)
            )
            
            // Get liveries
            val liveries = listOf(
                Livery("Classic", "#003366"),
                Livery("Safari", "#1a5c00"),
                Livery("Kenya", "#5c0000"),
                Livery("🔒 Locked", "#333333", isLocked = true)
            )
            
            _uiState.update { state ->
                state.copy(
                    vehicles = ownedVehicles,
                    selectedVehicleId = selectedVehicleId,
                    selectedVehicle = ownedVehicles.find { it.vehicleId == selectedVehicleId },
                    upgrades = upgrades,
                    inventory = inventory,
                    liveries = liveries,
                    isLoading = false
                )
            }
        }
    }
    
    private suspend fun getUpgradesForVehicle(vehicleId: Int): List<UpgradeModule> {
        // In a real implementation, this would fetch upgrades from a repository
        // For now, return mock data
        return listOf(
            UpgradeModule(
                upgradeId = "engine_boost",
                displayName = "Engine Boost",
                description = "Increases max speed by +5 km/h and acceleration rate by 15% per level.",
                icon = "⚡",
                currentLevel = 4,
                maxLevel = 8,
                cost = 3200,
                progress = 0.5f
            ),
            UpgradeModule(
                upgradeId = "seat_capacity",
                displayName = "Seat Capacity",
                description = "Add +4 passenger seats per level. Max capacity increases revenue potential.",
                icon = "🪑",
                currentLevel = 3,
                maxLevel = 6,
                cost = 2100,
                progress = 0.5f
            ),
            UpgradeModule(
                upgradeId = "fuel_efficiency",
                displayName = "Fuel Efficiency",
                description = "Reduces fuel consumption by 8% per level. Critical for long rural routes.",
                icon = "⛽",
                currentLevel = 5,
                maxLevel = 8,
                cost = 1800,
                progress = 0.625f
            ),
            UpgradeModule(
                upgradeId = "suspension",
                displayName = "Suspension",
                description = "Improves ride comfort and reduces vehicle wear on rural roads.",
                icon = "🛡",
                currentLevel = 6,
                maxLevel = 6,
                cost = 0,
                progress = 1f,
                isMaxed = true
            ),
            UpgradeModule(
                upgradeId = "gps_nav",
                displayName = "GPS Navigation",
                description = "Improves pathfinding accuracy and reduces wrong-turn penalties.",
                icon = "📡",
                currentLevel = 2,
                maxLevel = 5,
                cost = 2600,
                progress = 0.4f
            ),
            UpgradeModule(
                upgradeId = "pa_system",
                displayName = "PA System",
                description = "Improves passenger satisfaction score. Higher satisfaction = increased tips.",
                icon = "🔊",
                currentLevel = 1,
                maxLevel = 4,
                cost = 900,
                progress = 0.25f
            )
        )
    }
    
    fun selectVehicle(vehicleId: Int) {
        selectedVehicleId = vehicleId
        viewModelScope.launch {
            val vehicles = playerRepository.getPlayerVehicles()
            val selected = vehicles.find { it.vehicleId == vehicleId }
            val upgrades = getUpgradesForVehicle(vehicleId)
            
            _uiState.update { state ->
                state.copy(
                    selectedVehicleId = vehicleId,
                    selectedVehicle = selected,
                    upgrades = upgrades
                )
            }
        }
    }
    
    fun upgradeModule(upgradeId: String) {
        viewModelScope.launch {
            try {
                // In a real implementation, this would call an UpgradeVehiclePartUseCase
                // For now, just update the local state
                val upgrades = _uiState.value.upgrades.map { upgrade ->
                    if (upgrade.upgradeId == upgradeId && !upgrade.isMaxed) {
                        val newLevel = upgrade.currentLevel + 1
                        val isMaxed = newLevel >= upgrade.maxLevel
                        upgrade.copy(
                            currentLevel = newLevel,
                            progress = newLevel.toFloat() / upgrade.maxLevel,
                            isMaxed = isMaxed
                        )
                    } else {
                        upgrade
                    }
                }
                _uiState.update { state ->
                    state.copy(upgrades = upgrades)
                }
                
                // Show success notification
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun editLivery() {
        // Navigate to livery editor
    }
    
    fun fullService() {
        viewModelScope.launch {
            try {
                val vehicleId = _uiState.value.selectedVehicleId
                if (vehicleId != null) {
                    val vehicle = playerRepository.getPlayerVehicle(vehicleId)
                    val updated = vehicle.copy(
                        conditionPct = 100f,
                        engineHealthPct = 100f,
                        tyreConditionPct = 100f,
                        lastMaintainedAt = System.currentTimeMillis().toString()
                    )
                    playerRepository.updateVehicle(updated)
                    loadGarageData()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

data class GarageUiState(
    val vehicles: List<Vehicle> = emptyList(),
    val selectedVehicleId: Int? = null,
    val selectedVehicle: Vehicle? = null,
    val upgrades: List<UpgradeModule> = emptyList(),
    val inventory: List<InventoryItem> = emptyList(),
    val liveries: List<Livery> = emptyList(),
    val isLoading: Boolean = false
)

data class UpgradeModule(
    val upgradeId: String,
    val displayName: String,
    val description: String,
    val icon: String,
    val currentLevel: Int,
    val maxLevel: Int,
    val cost: Int,
    val progress: Float,
    val isMaxed: Boolean = false
)

data class InventoryItem(
    val name: String,
    val icon: String,
    val quantity: Int
)

data class Livery(
    val name: String,
    val color: String,
    val isLocked: Boolean = false
)