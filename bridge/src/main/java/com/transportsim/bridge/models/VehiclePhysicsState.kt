package com.transportsim.bridge.models

import com.transportsim.bridge.annotations.Keep

/**
 * 1 kHz simulation output from C++ physics engine.
 * All fields are @Keep for JNI access.
 */
@Keep
data class VehiclePhysicsState(
    val posX: Float,
    val posY: Float,
    val posZ: Float,
    val headingDeg: Float,
    val speedKph: Float,
    val gear: Int,
    val rpm: Float,
    val fuelL: Float,
    val engineTempC: Float,
    val suspensionDeflectionFL: Float,
    val suspensionDeflectionFR: Float,
    val suspensionDeflectionRL: Float,
    val suspensionDeflectionRR: Float,
    val score: Int,
    val odometerKm: Float,
    val throttleInput: Float,
    val brakeInput: Float,
    val steerAngleDeg: Float
) {
    companion object {
        fun empty() = VehiclePhysicsState(
            posX = 0f, posY = 0f, posZ = 0f,
            headingDeg = 0f, speedKph = 0f,
            gear = 0, rpm = 0f,
            fuelL = 0f, engineTempC = 0f,
            suspensionDeflectionFL = 0f,
            suspensionDeflectionFR = 0f,
            suspensionDeflectionRL = 0f,
            suspensionDeflectionRR = 0f,
            score = 0, odometerKm = 0f,
            throttleInput = 0f, brakeInput = 0f, steerAngleDeg = 0f
        )
    }
}