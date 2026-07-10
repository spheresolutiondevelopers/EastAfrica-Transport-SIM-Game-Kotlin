package com.transportsim.domain.models

data class Upgrade(
    val upgradeId: String,
    val displayName: String,
    val description: String,
    val maxLevel: Int,
    val costPerLevel: Int,
    val effectPerLevel: Float,    // multiplier or additive
    val effectType: EffectType
)

enum class EffectType {
    SPEED_BOOST,
    CAPACITY_BOOST,
    FUEL_EFFICIENCY,
    BRAKING_BOOST,
    SUSPENSION_BOOST,
    NAVIGATION_BOOST,
    PASSENGER_SATISFACTION
}

data class VehicleUpgrade(
    val vehicleId: Int,
    val upgradeId: String,
    val currentLevel: Int
)