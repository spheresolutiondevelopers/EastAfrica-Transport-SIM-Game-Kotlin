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

            // Ensure default vehicle exists (seeding logic)
            val defaultVehicle = playerRepository.ensureDefaultVehicle()
            val vehicles = playerRepository.getPlayerVehicles()
            
            val selectedVehicle = vehicles.find { it.vehicleId == selectedVehicleId }
            val selectedVehicleSpec = if (selectedVehicle != null) {
                catalogRepository.getVehicleSpec(selectedVehicle.typeId)
            } else {
                null
            }

            // Get upgrades for selected vehicle
            val upgrades = if (selectedVehicleId != null) {
                getUpgradesForVehicle(selectedVehicleId!!)
            } else {
                emptyList()
            }

            // Get inventory (mock data for now)
            val inventory = listOf(
                InventoryItem("Engine Parts", "🔩", 3),
                InventoryItem("Tyres", "🛞", 8),
                InventoryItem("Battery", "🔋", 2),
                InventoryItem("Oil Filter", "🛢", 0)
            )

            // Get liveries (mock data for now)
            val liveries = listOf(
                Livery("Classic", "#003366"),
                Livery("Safari", "#1a5c00"),
                Livery("Kenya", "#5c0000"),
                Livery("🔒 Locked", "#333333", isLocked = true)
            )

            _uiState.update { state ->
                state.copy(
                    vehicles = vehicles,
                    selectedVehicleId = selectedVehicleId,
                    selectedVehicle = selectedVehicle,
                    selectedVehicleSpec = selectedVehicleSpec,
                    upgrades = upgrades,
                    inventory = inventory,
                    liveries = liveries,
                    isLoading = false
                )
            }
        }
    }

    private suspend fun getUpgradesForVehicle(vehicleId: Int): List<UpgradeWithLevel> {
        val definitions = catalogRepository.getUpgradeDefinitions()
        val vehicleUpgrades = playerRepository.getUpgradesForVehicle(vehicleId)
        val upgradeMap = vehicleUpgrades.associateBy { it.upgradeId }

        return definitions.map { def ->
            val level = upgradeMap[def.upgradeId]?.currentLevel ?: 0
            UpgradeWithLevel(def, level)
        }
    }

    fun selectVehicle(vehicleId: Int) {
        selectedVehicleId = vehicleId
        loadGarageData()
    }

    fun upgradeModule(upgradeId: String) {
        viewModelScope.launch {
            val vehicleId = _uiState.value.selectedVehicleId ?: return@launch

            val upgrades = _uiState.value.upgrades
            val upgrade = upgrades.find { it.definition.upgradeId == upgradeId } ?: return@launch

            if (upgrade.isMaxed) return@launch

            val cost = upgrade.nextCost
            val balance = economyRepository.getBalance()
            if (balance < cost) {
                // Handle insufficient balance error (e.g., show Toast via UI event)
                return@launch
            }

            // Deduct balance and apply upgrade
            val result = playerRepository.applyUpgrade(vehicleId, upgradeId)
            if (result.isSuccess) {
                economyRepository.addTransaction(-cost, "UPGRADE", "${upgrade.definition.displayName} Lv.${upgrade.currentLevel + 1}")
                loadGarageData() // Refresh UI
            }
        }
    }

    fun editLivery() {
        // Navigate to livery editor
    }

    fun fullService() {
        viewModelScope.launch {
            val vehicleId = _uiState.value.selectedVehicleId ?: return@launch
            
            val cost = 2000
            val balance = economyRepository.getBalance()
            if (balance < cost) return@launch

            val result = playerRepository.fullServiceVehicle(vehicleId)
            if (result.isSuccess) {
                economyRepository.addTransaction(-cost, "MAINTENANCE", "Full Service")
                loadGarageData()
            }
        }
    }
}

data class GarageUiState(
    val vehicles: List<Vehicle> = emptyList(),
    val selectedVehicleId: Int? = null,
    val selectedVehicle: Vehicle? = null,
    val selectedVehicleSpec: VehicleCatalogEntry? = null,
    val upgrades: List<UpgradeWithLevel> = emptyList(),
    val inventory: List<InventoryItem> = emptyList(),
    val liveries: List<Livery> = emptyList(),
    val isLoading: Boolean = false
)

data class InventoryItem(val name: String, val icon: String, val quantity: Int)
data class Livery(val name: String, val color: String, val isLocked: Boolean = false)
