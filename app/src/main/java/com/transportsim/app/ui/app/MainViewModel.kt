package com.transportsim.app.ui.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transportsim.domain.repositories.EconomyRepository
import com.transportsim.domain.repositories.FleetRepository
import com.transportsim.domain.repositories.PlayerRepository
import com.transportsim.domain.models.VehicleStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GlobalUiState(
    val balance: Int = 0,
    val level: Int = 1,
    val xp: Int = 0,
    val fleetSize: Int = 0,
    val activeSize: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val economyRepository: EconomyRepository,
    private val fleetRepository: FleetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GlobalUiState())
    val uiState: StateFlow<GlobalUiState> = _uiState.asStateFlow()

    init {
        observeGlobalData()
    }

    private fun observeGlobalData() {
        viewModelScope.launch {
            combine(
                playerRepository.observeProfile(),
                economyRepository.observeBalance(),
                fleetRepository.observeFleet()
            ) { profile, balance, fleet ->
                GlobalUiState(
                    balance = balance,
                    level = profile.level,
                    xp = profile.xp,
                    fleetSize = fleet.size,
                    activeSize = fleet.count { it.status == VehicleStatus.ACTIVE },
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
}
