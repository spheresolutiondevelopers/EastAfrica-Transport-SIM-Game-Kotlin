package com.transportsim.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class RouteWaypoint(
    val name: String,
    val distanceKm: Double,
    val isTerminal: Boolean = false
)