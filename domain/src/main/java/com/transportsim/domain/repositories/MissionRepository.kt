package com.transportsim.domain.repositories

import com.transportsim.domain.models.Mission
import kotlinx.coroutines.flow.Flow

interface MissionRepository {
    suspend fun getAvailableMissions(): List<Mission>
    suspend fun getActiveMissions(): List<Mission>
    suspend fun acceptMission(missionId: String): Result<Mission>
    suspend fun completeMission(missionId: String): Result<Unit>
    suspend fun failMission(missionId: String): Result<Unit>
    suspend fun generateDailyMissions(): List<Mission>
    fun observeMissions(): Flow<List<Mission>>
}