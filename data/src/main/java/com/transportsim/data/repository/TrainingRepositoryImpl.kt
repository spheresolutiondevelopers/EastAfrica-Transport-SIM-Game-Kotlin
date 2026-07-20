package com.transportsim.data.repository

import com.transportsim.data.database.dao.TrainingProgressDao
import com.transportsim.data.database.entities.TrainingProgressEntity
import com.transportsim.data.datasource.AssetDataSource
import com.transportsim.domain.models.TrainingProgress
import com.transportsim.domain.models.TrainingScenario
import com.transportsim.domain.models.TrainingScenarioWithProgress
import com.transportsim.domain.repositories.TrainingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainingRepositoryImpl @Inject constructor(
    private val assetDataSource: AssetDataSource,
    private val progressDao: TrainingProgressDao
) : TrainingRepository {

    private var scenarioCache: List<TrainingScenario>? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())

    override suspend fun getAllScenarios(): List<TrainingScenario> {
        if (scenarioCache == null) {
            scenarioCache = assetDataSource.loadTrainingScenarios()
        }
        return scenarioCache ?: emptyList()
    }

    override suspend fun getScenariosWithProgress(): List<TrainingScenarioWithProgress> {
        val scenarios = getAllScenarios()
        val progressEntities = progressDao.getAll().associateBy { it.scenarioId }

        return scenarios.map { scenario ->
            val progressEntity = progressEntities[scenario.id]
            val progress = if (progressEntity != null) {
                TrainingProgress(
                    scenarioId = progressEntity.scenarioId,
                    isUnlocked = progressEntity.isUnlocked,
                    isCompleted = progressEntity.isCompleted,
                    bestScore = progressEntity.bestScore,
                    timesCompleted = progressEntity.timesCompleted,
                    stars = progressEntity.stars,
                    lastPlayedAt = progressEntity.lastPlayedAt,
                    unlockLevel = progressEntity.unlockLevel
                )
            } else {
                // Create default progress
                val defaultProgress = TrainingProgress(
                    scenarioId = scenario.id,
                    isUnlocked = scenario.unlockLevel == 0,
                    isCompleted = false,
                    unlockLevel = scenario.unlockLevel
                )
                // Save default to database
                progressDao.insertOrUpdate(
                    TrainingProgressEntity(
                        scenarioId = scenario.id,
                        isUnlocked = scenario.unlockLevel == 0,
                        unlockLevel = scenario.unlockLevel
                    )
                )
                defaultProgress
            }
            TrainingScenarioWithProgress(scenario, progress)
        }
    }

    override suspend fun getScenario(id: String): TrainingScenario? {
        return getAllScenarios().find { it.id == id }
    }

    override suspend fun unlockScenario(id: String): Result<Unit> {
        return runCatching {
            progressDao.unlockScenario(id)
        }
    }

    override suspend fun recordCompletion(
        scenarioId: String,
        score: Int,
        stars: Int
    ): Result<Unit> {
        return runCatching {
            val playedAt = dateFormat.format(Date())
            progressDao.recordCompletion(scenarioId, score, stars, playedAt)
        }
    }

    override fun observeScenarios(): Flow<List<TrainingScenarioWithProgress>> {
        // In a real implementation, this would observe from a data source
        return flowOf(emptyList())
    }
}
