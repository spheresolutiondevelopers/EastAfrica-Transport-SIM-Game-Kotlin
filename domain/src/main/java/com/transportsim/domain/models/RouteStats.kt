package com.transportsim.domain.models

data class RouteStats(
    val routeId: String,
    val timesCompleted: Int,
    val bestScore: Int,
    val totalDistanceKm: Double,
    val totalRevenueKsh: Int,
    val totalXP: Int,
    val fastestTimeSeconds: Int,
    val lastPlayedAt: String?,
    val stars: Int
) {
    val hasBeenPlayed: Boolean get() = timesCompleted > 0
    val averageScore: Int get() = if (timesCompleted > 0) bestScore else 0
    val formattedFastestTime: String get() {
        if (fastestTimeSeconds == Int.MAX_VALUE) return "--:--"
        val minutes = fastestTimeSeconds / 60
        val seconds = fastestTimeSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }
}