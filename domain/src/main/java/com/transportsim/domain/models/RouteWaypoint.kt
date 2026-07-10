package com.transportsim.domain.models

data class RouteWaypoint(
    val name: String,
    val distanceFromOriginKm: Double,
    val lat: Double,
    val lng: Double,
    val worldX: Double,
    val worldZ: Double,
    val isTerminal: Boolean = false
)