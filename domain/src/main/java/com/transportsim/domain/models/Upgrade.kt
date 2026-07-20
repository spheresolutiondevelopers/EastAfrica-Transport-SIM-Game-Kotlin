package com.transportsim.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class UpgradeDefinition(
    val upgradeId: String,
    val displayName: String,
    val description: String,
    val icon: String,
    val maxLevel: Int,
    val costPerLevel: List<Int>,
    val effectPerLevel: List<Float>,
    val effectType: EffectType,
    val effectUnit: String
)

enum class EffectType {
    SPEED_BOOST,
    TRACTION_BOOST,
    GROUND_CLEARANCE,
    BRAKING_BOOST,
    FUEL_CAPACITY,
    SUSPENSION_BOOST,
    NAVIGATION_BOOST,
    PASSENGER_SATISFACTION,
    CAPACITY_BOOST,
    FUEL_EFFICIENCY
}

data class VehicleUpgrade(
    val vehicleId: Int,
    val upgradeId: String,
    val currentLevel: Int
)

data class UpgradeWithLevel(
    val definition: UpgradeDefinition,
    val currentLevel: Int
) {
    val isMaxed: Boolean get() = currentLevel >= definition.maxLevel
    val nextCost: Int get() = if (currentLevel < definition.maxLevel) 
        definition.costPerLevel[currentLevel] else 0
    val progress: Float get() = if (definition.maxLevel > 0) 
        currentLevel.toFloat() / definition.maxLevel else 0f
    val effectValue: Float get() = if (currentLevel > 0) 
        definition.effectPerLevel[currentLevel - 1] else 0f
    val displayEffect: String get() = "${effectValue} ${definition.effectUnit}"
}
