package com.transportsim.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stop_events")
data class StopEventEntity(
    @PrimaryKey(autoGenerate = true)
    val eventId: Int = 0,
    val sessionId: Int,
    val stopId: Int?,
    val stopName: String?,
    val arrivalTime: String,
    val departureTime: String?,
    val dwellActualSec: Double?,
    val onTime: Boolean?,
    val passengersBoarded: Int?,
    val passengersAlighted: Int?
)