package com.transportsim.app.ui.training

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transportsim.domain.models.TrainingScenarioWithProgress
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

            val scenarios = trainingRepository.getScenariosWithProgress()
            
            // Get player profile for unlock info
            val profile = playerRepository.getProfile()
            val playerLevel = profile.level

            // Check which scenarios should be auto-unlocked based on level
            scenarios.forEach { scenarioWithProgress ->
                if (!scenarioWithProgress.isUnlocked && scenarioWithProgress.scenario.unlockLevel <= playerLevel) {
                    trainingRepository.unlockScenario(scenarioWithProgress.scenario.id)
                }
            }

            // Refresh after potential unlocks
            val updatedScenarios = trainingRepository.getScenariosWithProgress()
            val completed = updatedScenarios.count { it.isCompleted }
            val total = updatedScenarios.size

            _uiState.update { state ->
                state.copy(
                    scenarios = updatedScenarios,
                    completedCount = completed,
                    totalCount = total,
                    playerLevel = playerLevel,
                    isLoading = false
                )
            }
        }
    }

    fun unlockScenario(scenarioId: String) {
        viewModelScope.launch {
            try {
                trainingRepository.unlockScenario(scenarioId)
                loadTrainingData()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun startTraining(scenarioId: String) {
        _uiState.update { state ->
            state.copy(
                showNotImplementedDialog = true,
                selectedScenarioId = scenarioId
            )
        }
    }

    fun dismissNotImplementedDialog() {
        _uiState.update { state ->
            state.copy(
                showNotImplementedDialog = false,
                selectedScenarioId = null
            )
        }
    }
}

data class TrainingUiState(
    val scenarios: List<TrainingScenarioWithProgress> = emptyList(),
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val playerLevel: Int = 1,
    val isLoading: Boolean = false,
    val showNotImplementedDialog: Boolean = false,
    val selectedScenarioId: String? = null
)
