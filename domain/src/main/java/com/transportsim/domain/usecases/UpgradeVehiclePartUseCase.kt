package com.transportsim.domain.usecases

import com.transportsim.domain.models.EffectType
import com.transportsim.domain.models.UpgradeDefinition
import com.transportsim.domain.models.VehicleUpgrade
import com.transportsim.domain.repositories.CatalogRepository
import com.transportsim.domain.repositories.EconomyRepository
import com.transportsim.domain.repositories.FleetRepository
import com.transportsim.domain.repositories.PlayerRepository

class UpgradeVehiclePartUseCase(
    private val catalogRepository: CatalogRepository,
    private val playerRepository: PlayerRepository,
    private val economyRepository: EconomyRepository,
    private val fleetRepository: FleetRepository
) {
    suspend operator fun invoke(vehicleId: Int, upgradeId: String): Result<UpgradeResult> {
        return runCatching {
            // 1. Get current vehicle and upgrade definitions
            val upgradeDef = catalogRepository.getUpgradeDefinition(upgradeId)
            val currentUpgrades = getCurrentUpgradeLevels(vehicleId)

            val currentLevel = currentUpgrades[upgradeId] ?: 0
            if (currentLevel >= upgradeDef.maxLevel) {
                throw IllegalStateException("Upgrade already at max level")
            }

            val nextLevel = currentLevel + 1
            val cost = upgradeDef.costPerLevel[currentLevel]

            // 2. Check balance
            val balance = economyRepository.getBalance()
            if (balance < cost) {
                throw IllegalStateException("Insufficient balance. Required: KSH $cost")
            }

            // 3. Apply upgrade to vehicle (Repository handles balance deduction in database)
            playerRepository.applyUpgrade(vehicleId, upgradeId).getOrThrow()
            
            // 4. Record transaction for ledger
            economyRepository.addTransaction(-cost, "UPGRADE", "${upgradeDef.displayName} Lv.$nextLevel")

            // 5. Return result
            val newProfile = playerRepository.getProfile()
            UpgradeResult(
                upgradeId = upgradeId,
                newLevel = nextLevel,
                cost = cost,
                newBalance = newProfile.balanceKsh,
                effect = calculateEffect(upgradeDef, nextLevel)
            )
        }
    }

    private suspend fun getCurrentUpgradeLevels(vehicleId: Int): Map<String, Int> {
        return playerRepository.getUpgradesForVehicle(vehicleId).associate { it.upgradeId to it.currentLevel }
    }

    private fun calculateEffect(upgrade: UpgradeDefinition, level: Int): String {
        val bonus = if (level > 0) upgrade.effectPerLevel[level - 1] else 0f
        return when (upgrade.effectType) {
            EffectType.SPEED_BOOST -> "+${bonus.toInt()} km/h"
            EffectType.TRACTION_BOOST -> "+${bonus}x traction"
            EffectType.GROUND_CLEARANCE -> "+${bonus} in"
            EffectType.BRAKING_BOOST -> "+${bonus.toInt()}% braking"
            EffectType.FUEL_CAPACITY -> "+${bonus.toInt()} L"
            EffectType.SUSPENSION_BOOST -> "+${bonus.toInt()}% comfort"
            EffectType.NAVIGATION_BOOST -> "+${bonus.toInt()}% accuracy"
            EffectType.PASSENGER_SATISFACTION -> "+${bonus.toInt()}% satisfaction"
            EffectType.CAPACITY_BOOST -> "+${bonus.toInt()} seats"
            EffectType.FUEL_EFFICIENCY -> "-${(bonus * 100).toInt()}% consumption"
        }
    }
}

data class UpgradeResult(
    val upgradeId: String,
    val newLevel: Int,
    val cost: Int,
    val newBalance: Int,
    val effect: String
)
