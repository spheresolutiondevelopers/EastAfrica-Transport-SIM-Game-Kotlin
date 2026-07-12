package com.transportsim.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "active_missions")
data class ActiveMissionEntity(
    @PrimaryKey
    val missionId: String,
    val title: String,
    val description: String,
    val type: String,   // CARGO, PASSENGER, EXPRESS, VIP, TRAINING
    val rewardKsh: Int,
    val xpReward: Int,
    val requiredVehicleCategory: String?,
    val minLevel: Int,
    val timeLimitMinutes: Int,
    val cargoCapacityKg: Int?,
    val passengerCount: Int?,
    val progressCurrent: Int = 0,
    val progressTarget: Int,
    val progressUnit: String,
    val expiresAt: String,
    val assignedVehicleId: Int?,
    val status: String = "IN_PROGRESS" // IN_PROGRESS, COMPLETED, FAILED
)