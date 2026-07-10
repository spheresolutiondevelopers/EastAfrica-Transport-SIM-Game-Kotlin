package com.transportsim.domain.models

data class ResolvedVehicleConfig(
    val vehicleId: Int,
    val typeId: String,
    // Mass & geometry
    val massKg: Float,
    val maxPayloadKg: Float,
    val wheelbaseM: Float,
    val comHeightLadenM: Float,
    val momentInertiaIzz: Float,
    // Pacejka (resolved)
    val pacejkaB: Float,
    val pacejkaC: Float,
    val pacejkaD: Float,
    val pacejkaE: Float,
    // Suspension (resolved)
    val frontSpringRate: Float,
    val rearSpringRate: Float,
    val frontDamping: Float,
    val rearDamping: Float,
    // Derived performance
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