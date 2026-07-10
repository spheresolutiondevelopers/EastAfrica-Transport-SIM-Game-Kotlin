package com.transportsim.domain.models

data class Vehicle(
    val vehicleId: Int,
    val typeId: String,
    val serialNumber: String,
    val displayName: String?,
    val status: VehicleStatus,
    val fuelLevelL: Float,
    val fuelLevelPct: Float,
    val conditionPct: Float,
    val engineHealthPct: Float,
    val tyreConditionPct: Float,
    val odometerKm: Float,
    val currentRouteId: String?,
    val purchasedAt: String,
    val lastMaintainedAt: String?,
    val isFavorite: Boolean
)

enum class VehicleStatus {
    IDLE, ACTIVE, GARAGE
}