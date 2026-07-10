package com.transportsim.domain.usecases

import com.transportsim.domain.models.Mission
import com.transportsim.domain.repositories.MissionRepository

class GenerateDailyMissionsUseCase(
    private val missionRepository: MissionRepository
) {
    suspend operator fun invoke(): List<Mission> =
        missionRepository.generateDailyMissions()
}