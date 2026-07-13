package com.transportsim.app.ui.missions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transportsim.domain.models.Mission
import com.transportsim.domain.repositories.MissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MissionDetailViewModel @Inject constructor(
    private val missionRepository: MissionRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MissionDetailUiState())
    val uiState: StateFlow<MissionDetailUiState> = _uiState.asStateFlow()
    
    fun loadMission(missionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Try to find mission in available or active missions
            val available = missionRepository.getAvailableMissions()
            val active = missionRepository.getActiveMissions()
            val mission = (available + active).find { it.missionId == missionId }
            
            _uiState.update { state ->
                state.copy(
                    mission = mission,
                    isLoading = false
                )
            }
        }
    }
}

data class MissionDetailUiState(
    val mission: Mission? = null,
    val isLoading: Boolean = false
)