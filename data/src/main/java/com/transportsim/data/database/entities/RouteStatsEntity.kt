package com.transportsim.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route_stats")
data class RouteStatsEntity(
    @PrimaryKey
    val routeId: String,
    val timesCompleted: Int = 0,
    val bestScore: Int = 0,
    val totalDistanceKm: Double = 0.0,
    val totalRevenueKsh: Int = 0,
    val totalXP: Int = 0,
    val fastestTimeSeconds: Int = Int.MAX_VALUE,
    val lastPlayedAt: String? = null,
    val stars: Int = 0
)