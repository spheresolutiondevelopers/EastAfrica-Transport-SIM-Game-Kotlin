package com.transportsim.domain.usecases

import com.transportsim.domain.repositories.EconomyRepository
import com.transportsim.domain.repositories.PlayerRepository
import kotlinx.coroutines.flow.first

class RepairVehicleUseCase(
    private val playerRepository: PlayerRepository,
    private val economyRepository: EconomyRepository
) {
    suspend operator fun invoke(vehicleId: Int, repairType: RepairType): Result<RepairResult> {
        return runCatching {
            val vehicle = playerRepository.getPlayerVehicle(vehicleId)
            val profile = playerRepository.getProfile()

            val cost = when (repairType) {
                RepairType.ENGINE -> 1500 + (100 - vehicle.engineHealthPct).toInt() * 20
                RepairType.TYRES -> 800 + (100 - vehicle.tyreConditionPct).toInt() * 10
                RepairType.FULL -> 3000 + (100 - vehicle.conditionPct).toInt() * 30
            }

            if (profile.balanceKsh < cost) {
                throw IllegalStateException("Insufficient balance. Required: KSH $cost")
            }

            // Deduct balance
            val updatedProfile = profile.copy(balanceKsh = profile.balanceKsh - cost)
            playerRepository.updateProfile(updatedProfile)
            economyRepository.addTransaction(-cost, "REPAIR", "$repairType repair on vehicle $vehicleId")

            // Update vehicle
            val updatedVehicle = when (repairType) {
                RepairType.ENGINE -> vehicle.copy(engineHealthPct = 100f)
                RepairType.TYRES -> vehicle.copy(tyreConditionPct = 100f)
                RepairType.FULL -> vehicle.copy(
                    conditionPct = 100f,
                    engineHealthPct = 100f,
                    tyreConditionPct = 100f
                )
            }
            playerRepository.updateVehicle(updatedVehicle)

            RepairResult(
                vehicleId = vehicleId,
                repairType = repairType,
                cost = cost,
                newBalance = updatedProfile.balanceKsh,
                newCondition = updatedVehicle.conditionPct,
                newEngineHealth = updatedVehicle.engineHealthPct,
                newTyreCondition = updatedVehicle.tyreConditionPct
            )
        }
    }
}

enum class RepairType { ENGINE, TYRES, FULL }

data class RepairResult(
    val vehicleId: Int,
    val repairType: RepairType,
    val cost: Int,
    val newBalance: Int,
    val newCondition: Float,
    val newEngineHealth: Float,
    val newTyreCondition: Float
)