package com.transportsim.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val id: String,
    val name: String,
    val description: String,
    val terrainType: TerrainType,
    val distanceKm: Double,
    val durationMin: Int,
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
    val isDlc: Boolean,
    val isTraining: Boolean,
    val revenuePerDayKsh: Int,
    val minVehicleClass: Int,
    val terrainTags: List<String>,
    val waypoints: List<RouteWaypoint>
)

@Serializable
enum class TerrainType {
    URBAN, HIGHWAY, EXPRESSWAY, RURAL, COASTAL
}