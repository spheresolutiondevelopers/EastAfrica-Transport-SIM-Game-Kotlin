package com.transportsim.data.repository

import com.transportsim.data.database.dao.ActiveMissionDao
import com.transportsim.data.database.entities.ActiveMissionEntity
import com.transportsim.data.datasource.AssetDataSource
import com.transportsim.domain.models.Mission
import com.transportsim.domain.models.MissionRequirements
import com.transportsim.domain.models.MissionStatus
import com.transportsim.domain.models.MissionType
import com.transportsim.domain.repositories.MissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MissionRepositoryImpl @Inject constructor(
    private val missionDao: ActiveMissionDao,
    private val assetDataSource: AssetDataSource
) : MissionRepository {

    private fun toDomain(entity: ActiveMissionEntity): Mission {
        return Mission(
            missionId = entity.missionId,
            title = entity.title,
            description = entity.description,
            type = MissionType.valueOf(entity.type),
            rewardKsh = entity.rewardKsh,
            xpReward = entity.xpReward,
            requirements = MissionRequirements(
                requiredVehicleCategory = entity.requiredVehicleCategory?.let { VehicleCategory.valueOf(it) },
                minLevel = entity.minLevel,
                timeLimitMinutes = entity.timeLimitMinutes,
                cargoCapacityKg = entity.cargoCapacityKg,
                passengerCount = entity.passengerCount
            ),
            progress = MissionProgress(
                current = entity.progressCurrent,
                target = entity.progressTarget,
                unit = entity.progressUnit
            ),
            expiresAt = entity.expiresAt,
            status = MissionStatus.valueOf(entity.status)
        )
    }

    private fun toEntity(domain: Mission): ActiveMissionEntity {
        return ActiveMissionEntity(
            missionId = domain.missionId,
            title = domain.title,
            description = domain.description,
            type = domain.type.name,
            rewardKsh = domain.rewardKsh,
            xpReward = domain.xpReward,
            requiredVehicleCategory = domain.requirements.requiredVehicleCategory?.name,
            minLevel = domain.requirements.minLevel,
            timeLimitMinutes = domain.requirements.timeLimitMinutes,
            cargoCapacityKg = domain.requirements.cargoCapacityKg,
            passengerCount = domain.requirements.passengerCount,
            progressCurrent = domain.progress.current,
            progressTarget = domain.progress.target,
            progressUnit = domain.progress.unit,
            expiresAt = domain.expiresAt ?: "",
            assignedVehicleId = null,
            status = domain.status.name
        )
    }

    override suspend fun getAvailableMissions(): List<Mission> {
        return missionDao.getAvailable().map { toDomain(it) }
    }

    override suspend fun getActiveMissions(): List<Mission> {
        return missionDao.getInProgress().map { toDomain(it) }
    }

    override suspend fun acceptMission(missionId: String): Result<Mission> {
        return runCatching {
            val entity = missionDao.getById(missionId)
                ?: throw IllegalArgumentException("Mission not found")
            entity.status = "IN_PROGRESS"
            missionDao.update(entity)
            toDomain(entity)
        }
    }

    override suspend fun completeMission(missionId: String): Result<Unit> {
        return runCatching {
            missionDao.markCompleted(missionId)
        }
    }

    override suspend fun failMission(missionId: String): Result<Unit> {
        return runCatching {
            missionDao.markFailed(missionId)
        }
    }

    override suspend fun generateDailyMissions(): List<Mission> {
        // Load templates from assets and insert new missions
        val templates = assetDataSource.loadMissionTemplates()
        val missions = templates.map { template ->
            // Create a new ActiveMissionEntity from the template
            val entity = ActiveMissionEntity(
                missionId = "${template.missionId}_${System.currentTimeMillis()}",
                title = template.title,
                description = template.description,
                type = template.type.name,
                rewardKsh = template.rewardKsh,
                xpReward = template.xpReward,
                requiredVehicleCategory = template.requirements.requiredVehicleCategory?.name,
                minLevel = template.requirements.minLevel,
                timeLimitMinutes = template.requirements.timeLimitMinutes,
                cargoCapacityKg = template.requirements.cargoCapacityKg,
                passengerCount = template.requirements.passengerCount,
                progressCurrent = 0,
                progressTarget = template.progress.target,
                progressUnit = template.progress.unit,
                expiresAt = "",
                assignedVehicleId = null,
                status = "AVAILABLE"
            )
            missionDao.insert(entity)
            toDomain(entity)
        }
        return missions
    }

    override fun observeMissions(): Flow<List<Mission>> {
        return missionDao.observeInProgress().map { entities ->
            entities.map { toDomain(it) }
        }
    }
}