package com.transportsim.domain.models

data class DailyPerformance(
    val date: String,
    val revenueKsh: Int,
    val passengers: Int,
    val cargoKg: Int,
    val trips: Int,
    val onTimeRatePct: Float,
    val distanceKm: Double
) {
    companion object {
        fun empty(date: String) = DailyPerformance(
            date = date,
            revenueKsh = 0,
            passengers = 0,
            cargoKg = 0,
            trips = 0,
            onTimeRatePct = 0f,
            distanceKm = 0.0
        )
    }
}
