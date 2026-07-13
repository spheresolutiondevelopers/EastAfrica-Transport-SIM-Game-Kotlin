package com.transportsim.domain.repositories

import com.transportsim.domain.models.TrainingScenario
import com.transportsim.domain.models.TrainingResult
import kotlinx.coroutines.flow.Flow

interface TrainingRepository {
    suspend fun getAllScenarios(): List<TrainingScenario>
    suspend fun getScenario(id: String): TrainingScenario?
    suspend fun unlockScenario(id: String): Result<Unit>
    suspend fun completeScenario(result: TrainingResult): Result<Unit>
    suspend fun getBestScore(scenarioId: String): Int?
    fun observeScenarios(): Flow<List<TrainingScenario>>
}