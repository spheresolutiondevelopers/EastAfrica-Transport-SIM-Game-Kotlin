package com.transportsim.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route_unlock_state")
data class RouteUnlockStateEntity(
    @PrimaryKey
    val routeId: String,
    val isUnlocked: Int = 0, // 0 = false, 1 = true
    val stars: Int = 0,
    val bestScore: Int = 0,
    val timesCompleted: Int = 0
)