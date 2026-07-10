package com.transportsim.domain.models

data class PhysicsStats(
    val speedKph: Float,
    val rpm: Float,
    val gear: Int,
    val fuelLevelPct: Float,
    val engineTempC: Float,
    val suspensionDeflections: List<Float>,
    val accelerationMs2: Float,
    val steeringAngleDeg: Float
)