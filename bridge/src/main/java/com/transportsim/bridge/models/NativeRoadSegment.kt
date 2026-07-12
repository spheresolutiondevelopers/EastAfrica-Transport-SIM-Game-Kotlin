package com.transportsim.bridge.models

import com.transportsim.bridge.annotations.Keep
import com.squareup.moshi.JsonClass

/**
 * Flat struct for road segment data passed to C++.
 * All fields are primitive for direct memory mapping.
 */
@Keep
@JsonClass(generateAdapter = true)
data class NativeRoadSegment(
    val segmentId: Int,
    val distAlongRouteM: Float,
    val segmentLengthM: Float,
    val frictionMuDry: Float,
    val frictionMuWet: Float,
    val iriValue: Float,
    val iriClass: Int,
    val superelevationDeg: Float,
    val speedLimitKph: Int,
    val lanes: Int,
    val roadWidthM: Float,
    val hasCrosswind: Boolean,
    val hasPothole: Boolean,
    val isIntersection: Boolean,
    val surfaceTypeId: String
)