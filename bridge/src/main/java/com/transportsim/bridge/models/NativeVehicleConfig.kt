package com.transportsim.bridge.models

import com.transportsim.bridge.annotations.Keep
import com.squareup.moshi.JsonClass

/**
 * Flat struct passed to C++ engine via JNI.
 * This is the native representation of ResolvedVehicleConfig.
 */
@Keep
@JsonClass(generateAdapter = true)
data class NativeVehicleConfig(
    // Identity
    val vehicleId: Int,
    val typeId: String,
    
    // Mass & geometry
    val massKg: Float,
    val maxPayloadKg: Float,
    val wheelbaseM: Float,
    val comHeightLadenM: Float,
    val momentInertiaIzz: Float,
    
    // Pacejka coefficients
    val pacejkaB: Float,
    val pacejkaC: Float,
    val pacejkaD: Float,
    val pacejkaE: Float,
    
    // Suspension
    val frontSpringRate: Float,
    val rearSpringRate: Float,
    val frontDamping: Float,
    val rearDamping: Float,
    
    // Performance
    val maxSpeedKph: Float,
    val passengerCapacity: Int,
    val fuelEffMult: Float,
    val brakeMs2: Float,
    val navAccuracyPct: Float,
    
    // Aero
    val dragCd: Float,
    val frontalAreaM2: Float,
    
    // Engine / fuel
    val enginePowerKw: Float,
    val peakTorqueNm: Float,
    val fuelCapacityL: Float,
    val fuelConsumptionBase: Float,
    
    // Live state
    val currentFuelL: Float,
    val engineHealthPct: Float,
    val tyreConditionPct: Float
)