package com.transportsim.app.ui.training

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transportsim.domain.models.TrainingScenario
import com.transportsim.domain.models.TrainingScenarioDifficulty
import com.transportsim.domain.repositories.PlayerRepository
import com.transportsim.domain.repositories.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val trainingRepository: TrainingRepository,
    private val playerRepository: PlayerRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TrainingUiState())
    val uiState: StateFlow<TrainingUiState> = _uiState.asStateFlow()
    
    init {
        loadTrainingData()
    }
    
    private fun loadTrainingData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Get all training scenarios
            val allScenarios = trainingRepository.getAllScenarios()
            
            // Get player progress
            val profile = playerRepository.getProfile()
            val playerLevel = profile.level
            
            // Determine unlocked scenarios based on level
            val scenarios = allScenarios.map { scenario ->
                scenario.copy(
                    isUnlocked = scenario.unlockLevel <= playerLevel
                )
            }
            
            val completed = scenarios.count { it.isCompleted }
            val total = scenarios.size
            
            _uiState.update { state ->
                state.copy(
                    scenarios = scenarios,
                    completedCount = completed,
                    totalCount = total,
                    isLoading = false
                )
            }
        }
    }
    
    fun unlockScenario(scenarioId: String) {
        viewModelScope.launch {
            try {
                trainingRepository.unlockScenario(scenarioId)
                loadTrainingData() // Refresh
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

data class TrainingUiState(
    val scenarios: List<TrainingScenario> = emptyList(),
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val isLoading: Boolean = false
)