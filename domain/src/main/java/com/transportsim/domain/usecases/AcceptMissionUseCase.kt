package com.transportsim.domain.usecases

import com.transportsim.domain.models.Mission
import com.transportsim.domain.models.MissionStatus
import com.transportsim.domain.repositories.MissionRepository
import com.transportsim.domain.repositories.PlayerRepository
import kotlinx.coroutines.flow.first

class AcceptMissionUseCase(
    private val missionRepository: MissionRepository,
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(missionId: String, vehicleId: Int? = null): Result<Mission> {
        return runCatching {
            // 1. Get the mission
            val allMissions = missionRepository.getAvailableMissions()
            val mission = allMissions.find { it.missionId == missionId }
                ?: throw IllegalArgumentException("Mission not found or not available")

            // 2. Check if player meets level requirement
            val profile = playerRepository.getProfile()
            if (profile.level < mission.requirements.minLevel) {
                throw IllegalStateException("Player level too low. Required: ${mission.requirements.minLevel}")
            }

            // 3. If a vehicle is required, check that we have it
            val requiredCategory = mission.requirements.requiredVehicleCategory
            if (requiredCategory != null) {
                val vehicles = playerRepository.getPlayerVehicles()
                val matchingVehicle = if (vehicleId != null) {
                    vehicles.find { it.vehicleId == vehicleId && it.typeId.startsWith(requiredCategory.name.lowercase()) }
                } else {
                    vehicles.find { it.typeId.startsWith(requiredCategory.name.lowercase()) && it.status == com.transportsim.domain.models.VehicleStatus.IDLE }
                }
                if (matchingVehicle == null) {
                    throw IllegalStateException("No suitable vehicle available for this mission")
                }
                // In a full implementation we'd assign the vehicle to the mission
            }

            // 4. Accept the mission
            missionRepository.acceptMission(missionId).getOrThrow()
        }
    }
}