package com.transportsim.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class VehicleCatalogEntry(
    val id: String,                      // "city_bus"
    val displayName: String,             // "City Bus"
    val emoji: String,                   // "🚌"
    val category: VehicleCategory,
    val massKg: Float,                   // 12000.0
    val maxPayloadKg: Float,             // 4800.0
    val passengerCapacity: Int,          // 48
    val cargoCapacityKg: Float,          // 0.0
    val wheelbaseM: Float,               // 5.8
    val trackWidthM: Float,              // 2.1
    val comHeightLadenM: Float,          // 1.45
    val momentInertiaIzz: Float,         // 98000.0
    val maxSpeedKph: Float,              // 90.0
    val peakAccelMs2: Float,             // 1.8
    val emergencyBrakeMs2: Float,        // 7.5
    val minTurnRadiusM: Float,           // 12.5
    val aeroDragCd: Float,               // 0.65
    val frontalAreaM2: Float,            // 6.8
    val enginePowerKw: Float,            // 180.0
    val peakTorqueNm: Float,             // 820.0
    val peakTorqueRpm: Int,              // 1400
    val fuelCapacityL: Float,            // 200.0
    val fuelConsumptionL100km: Float,    // 28.0
    val pacejka: PacejkaCoeffs,
    val suspension: SuspensionSpec,
    val assetFileBase: String,           // "city_bus.glb"
    val purchaseCostKsh: Int,            // 850000
    val unlockLevel: Int                 // 1
)

@Serializable
data class PacejkaCoeffs(
    val b: Float,
    val c: Float,
    val d: Float,
    val e: Float
)

@Serializable
data class SuspensionSpec(
    val frontSpringNm: Float,
    val rearSpringNm: Float,
    val frontDampingNsm: Float,
    val rearDampingNsm: Float
)