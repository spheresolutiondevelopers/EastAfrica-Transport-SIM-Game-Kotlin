package com.transportsim.domain.models

data class PlayerProfile(
    val playerId: String,
    val level: Int,
    val xp: Int,
    val balanceKsh: Int,
    val totalDistanceKm: Double,
    val totalPassengers: Int,
    val totalRevenue: Int,
    val lastPlayedAt: String?,
    val createdAt: String
)