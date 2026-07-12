package com.transportsim.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey
    val playerId: String = "default",
    val level: Int = 1,
    val xp: Int = 0,
    val balanceKsh: Int = 0,
    val totalDistanceKm: Double = 0.0,
    val totalPassengers: Int = 0,
    val totalRevenue: Int = 0,
    val lastPlayedAt: String? = null,
    val createdAt: String = System.currentTimeMillis().toString()
)