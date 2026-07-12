package com.transportsim.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicle_upgrades", primaryKeys = ["vehicleId", "upgradeId"])
data class VehicleUpgradeEntity(
    val vehicleId: Int,
    val upgradeId: String,
    val currentLevel: Int = 0
)