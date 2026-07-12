package com.transportsim.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journey_sessions")
data class JourneySessionEntity(
    @PrimaryKey(autoGenerate = true)
    val sessionId: Int = 0,
    val vehicleId: Int,
    val routeId: String,
    val startedAt: String,
    val endedAt: String? = null,
    val durationSec: Int? = null,
    val distanceKm: Double? = null,
    val score: Int? = null,
    val revenueKsh: Int? = null,
    val xpEarned: Int? = null,
    val passengers: Int? = null,
    val fuelUsedL: Float? = null,
    val status: String = "ACTIVE" // "ACTIVE", "COMPLETED", "ABANDONED"
)