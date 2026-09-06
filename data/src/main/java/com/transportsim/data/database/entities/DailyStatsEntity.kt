package com.transportsim.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_stats")
data class DailyStatsEntity(
    @PrimaryKey
    val statDate: String, // yyyy-MM-dd
    val revenueKsh: Int = 0,
    val passengers: Int = 0,
    val cargoKg: Int = 0,
    val distanceKm: Double = 0.0,
    val trips: Int = 0,
    val avgScore: Double = 0.0,
    val onTimeRatePct: Float = 0f,
    val topVehicleId: Int? = null
)
