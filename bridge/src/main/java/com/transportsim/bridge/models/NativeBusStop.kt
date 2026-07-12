package com.transportsim.bridge.models

import com.transportsim.bridge.annotations.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class NativeBusStop(
    val stopId: Int,
    val stopOrder: Int,
    val stopName: String,
    val worldX: Float,
    val worldZ: Float,
    val distFromOriginKm: Float,
    val dwellTimeS: Int,
    val onTimeToleranceM: Float,
    val maxPassengers: Int,
    val passengerDemandPeak: Float,
    val stopType: String,
    val hasShelter: Boolean,
    val isTerminal: Boolean
)