package com.transportsim.domain.usecases

import com.transportsim.domain.models.EffectType
import com.transportsim.domain.models.Upgrade
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
            val vehicle = playerRepository.getPlayerVehicle(vehicleId)
            val upgradeDef = catalogRepository.getUpgradeDefinition(upgradeId)
            val currentUpgrades = getCurrentUpgradeLevels(vehicleId)

            val currentLevel = currentUpgrades[upgradeId] ?: 0
            if (currentLevel >= upgradeDef.maxLevel) {
                throw IllegalStateException("Upgrade already at max level")
            }

            val nextLevel = currentLevel + 1
            val cost = upgradeDef.costPerLevel * nextLevel

            // 2. Check balance
            val profile = playerRepository.getProfile()
            if (profile.balanceKsh < cost) {
                throw IllegalStateException("Insufficient balance. Required: KSH $cost")
            }

            // 3. Deduct balance
            val updatedProfile = profile.copy(balanceKsh = profile.balanceKsh - cost)
            playerRepository.updateProfile(updatedProfile)
            economyRepository.addTransaction(-cost, "UPGRADE", "${upgradeDef.displayName} Lv.$nextLevel")

            // 4. Apply upgrade to vehicle
            applyUpgrade(vehicleId, upgradeId, nextLevel)

            // 5. Return result
            UpgradeResult(
                upgradeId = upgradeId,
                newLevel = nextLevel,
                cost = cost,
                newBalance = updatedProfile.balanceKsh,
                effect = calculateEffect(upgradeDef, nextLevel)
            )
        }
    }

    private suspend fun getCurrentUpgradeLevels(vehicleId: Int): Map<String, Int> {
        // In a real implementation this would come from a dedicated UpgradeRepository
        // For now we simulate with a simple call to fleet repository if available
        // Assume we have a method in fleetRepository to get upgrades
        return emptyMap() // Placeholder
    }

    private suspend fun applyUpgrade(vehicleId: Int, upgradeId: String, level: Int) {
        // In a real implementation we would call UpgradeRepository.saveUpgrade
        // This is a placeholder
    }

    private fun calculateEffect(upgrade: Upgrade, level: Int): String {
        val bonus = upgrade.effectPerLevel * level
        return when (upgrade.effectType) {
            EffectType.SPEED_BOOST -> "+${bonus.toInt()} km/h"
            EffectType.CAPACITY_BOOST -> "+${bonus.toInt()} seats"
            EffectType.FUEL_EFFICIENCY -> "-${(bonus * 100).toInt()}% consumption"
            EffectType.BRAKING_BOOST -> "+${bonus.toInt()}% braking force"
            EffectType.SUSPENSION_BOOST -> "+${bonus.toInt()}% ride comfort"
            EffectType.NAVIGATION_BOOST -> "+${bonus.toInt()}% accuracy"
            EffectType.PASSENGER_SATISFACTION -> "+${bonus.toInt()}% satisfaction"
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