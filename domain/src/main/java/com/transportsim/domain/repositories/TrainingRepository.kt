package com.transportsim.domain.repositories

import com.transportsim.domain.models.TrainingScenario
import com.transportsim.domain.models.TrainingScenarioWithProgress
import kotlinx.coroutines.flow.Flow

interface TrainingRepository {
    suspend fun getAllScenarios(): List<TrainingScenario>
    suspend fun getScenariosWithProgress(): List<TrainingScenarioWithProgress>
    suspend fun getScenario(id: String): TrainingScenario?
    suspend fun unlockScenario(id: String): Result<Unit>
    suspend fun recordCompletion(
        scenarioId: String,
        score: Int,
        stars: Int
    ): Result<Unit>
    fun observeScenarios(): Flow<List<TrainingScenarioWithProgress>>
}
