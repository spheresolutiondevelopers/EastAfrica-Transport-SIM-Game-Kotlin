package com.transportsim.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_vehicles")
data class PlayerVehicleEntity(
    @PrimaryKey(autoGenerate = true)
    val vehicleId: Int = 0,
    val typeId: String,                  // "city_bus"
    val serialNumber: String,
    val displayName: String?,
    val status: String,                  // "IDLE", "ACTIVE", "GARAGE"
    val fuelLevelL: Float = 0f,
    val fuelLevelPct: Float = 0f,
    val conditionPct: Float = 100f,
    val engineHealthPct: Float = 100f,
    val tyreConditionPct: Float = 100f,
    val odometerKm: Float = 0f,
    val currentRouteId: String?,
    val purchasedAt: String,
    val lastMaintainedAt: String?,
    val isFavorite: Int = 0   // 0 = false, 1 = true
)