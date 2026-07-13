package com.transportsim.app.ui.missions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transportsim.domain.models.Mission
import com.transportsim.domain.models.DailyReward
import com.transportsim.domain.repositories.EconomyRepository
import com.transportsim.domain.repositories.MissionRepository
import com.transportsim.domain.repositories.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MissionsViewModel @Inject constructor(
    private val missionRepository: MissionRepository,
    private val playerRepository: PlayerRepository,
    private val economyRepository: EconomyRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MissionsUiState())
    val uiState: StateFlow<MissionsUiState> = _uiState.asStateFlow()
    
    private var filterType: String? = null
    private var timerJob: kotlinx.coroutines.Job? = null
    
    init {
        loadMissions()
        startTimer()
    }
    
    private fun loadMissions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Get missions
            val available = missionRepository.getAvailableMissions()
            val active = missionRepository.getActiveMissions()
            
            // Apply filter
            val filteredAvailable = if (filterType != null) {
                available.filter { it.type.name == filterType }
            } else {
                available
            }
            
            val filteredActive = if (filterType != null) {
                active.filter { it.type.name == filterType }
            } else {
                active
            }
            
            // Get reward track
            val rewardTrack = economyRepository.getDailyRewards()
            val rewards = rewardTrack.rewards
            val currentStep = rewardTrack.currentStep
            
            // Get leaderboard (simplified)
            val leaderboard = listOf(
                LeaderboardEntry("🏆 SpeedKing254", 142000),
                LeaderboardEntry("🥇 NairobiFleet", 128000),
                LeaderboardEntry("🥈 MombasaRun", 119000),
                LeaderboardEntry("🥉 You", 88000)
            )
            
            _uiState.update { state ->
                state.copy(
                    availableMissions = filteredAvailable,
                    activeMissions = filteredActive,
                    rewards = rewards,
                    rewardStep = currentStep,
                    totalRewardSteps = rewards.size,
                    leaderboard = leaderboard,
                    isLoading = false
                )
            }
        }
    }
    
    private fun startTimer() {
        timerJob = viewModelScope.launch {
            var seconds = 29662 // 8h 14m 22s remaining
            while (true) {
                val hours = seconds / 3600
                val minutes = (seconds % 3600) / 60
                val secs = seconds % 60
                _uiState.update { state ->
                    state.copy(
                        resetTimer = String.format("%02d:%02d:%02d", hours, minutes, secs)
                    )
                }
                kotlinx.coroutines.delay(1000)
                seconds--
                if (seconds < 0) seconds = 29662
            }
        }
    }
    
    fun setFilter(type: String?) {
        filterType = type
        loadMissions()
    }
    
    fun acceptMission(missionId: String) {
        viewModelScope.launch {
            try {
                missionRepository.acceptMission(missionId)
                loadMissions()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun completeMission(missionId: String) {
        viewModelScope.launch {
            try {
                missionRepository.completeMission(missionId)
                loadMissions()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun claimReward() {
        viewModelScope.launch {
            try {
                economyRepository.claimDailyReward()
                loadMissions()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

data class MissionsUiState(
    val availableMissions: List<Mission> = emptyList(),
    val activeMissions: List<Mission> = emptyList(),
    val rewards: List<DailyReward> = emptyList(),
    val rewardStep: Int = 0,
    val totalRewardSteps: Int = 7,
    val leaderboard: List<LeaderboardEntry> = emptyList(),
    val resetTimer: String = "00:00:00",
    val isLoading: Boolean = false
)

data class LeaderboardEntry(
    val name: String,
    val xp: Int
)