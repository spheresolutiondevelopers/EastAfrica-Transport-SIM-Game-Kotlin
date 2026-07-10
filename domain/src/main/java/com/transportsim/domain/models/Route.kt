package com.transportsim.domain.models

data class Route(
    val routeId: String,                 // "KE-001"
    val name: String,
    val distanceKm: Double,
    val durationMin: Int,
    val terrainType: TerrainType,
    val originLat: Double,
    val originLng: Double,
    val originElev: Double,
    val destLat: Double,
    val destLng: Double,
    val destElev: Double,
    val originWorldX: Double,
    val originWorldZ: Double,
    val maxSpeedZoneKph: Int,
    val unlockLevel: Int,
    val revenuePerDayKsh: Int,
    val minVehicleClass: Int,
    val terrainTags: List<String>,
    val isDlc: Boolean
)

enum class TerrainType {
    URBAN, HIGHWAY, EXPRESSWAY, RURAL, COASTAL
}