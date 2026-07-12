package com.transportsim.data.config

import com.transportsim.domain.models.ResolvedVehicleConfig
import com.transportsim.domain.repositories.CatalogRepository
import com.transportsim.domain.repositories.PlayerRepository
import com.transportsim.domain.repositories.FleetRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VehicleConfigResolver @Inject constructor(
    private val catalogRepository: CatalogRepository,
    private val playerRepository: PlayerRepository,
    private val fleetRepository: FleetRepository
) {
    suspend fun resolve(vehicleId: Int): ResolvedVehicleConfig {
        val vehicle = playerRepository.getPlayerVehicle(vehicleId)
        val base = catalogRepository.getVehicleSpec(vehicle.typeId)

        // In a full implementation, we'd fetch upgrades from a dedicated upgrade repository.
        // For now, assume no upgrades applied.
        // val upgrades = upgradeRepository.getUpgradesForVehicle(vehicleId).associateBy { it.upgradeId }
        // fun lvl(id: String) = upgrades[id]?.currentLevel ?: 0

        // Pacejka D: base × tyre wear fraction (and optionally upgrade bonus)
        val pacejkaD = base.pacejka.d * (vehicle.tyreConditionPct / 100f)

        return ResolvedVehicleConfig(
            vehicleId = vehicleId,
            typeId = vehicle.typeId,
            massKg = base.massKg,
            maxPayloadKg = base.maxPayloadKg,
            wheelbaseM = base.wheelbaseM,
            comHeightLadenM = base.comHeightLadenM,
            momentInertiaIzz = base.momentInertiaIzz,
            pacejkaB = base.pacejka.b,
            pacejkaC = base.pacejka.c,
            pacejkaD = pacejkaD,
            pacejkaE = base.pacejka.e,
            frontSpringRate = base.suspension.frontSpringNm,
            rearSpringRate = base.suspension.rearSpringNm,
            frontDamping = base.suspension.frontDampingNsm,
            rearDamping = base.suspension.rearDampingNsm,
            maxSpeedKph = base.maxSpeedKph,
            passengerCapacity = base.passengerCapacity,
            fuelEffMult = 1f,
            brakeMs2 = base.emergencyBrakeMs2,
            navAccuracyPct = 100f,
            dragCd = base.aeroDragCd,
            frontalAreaM2 = base.frontalAreaM2,
            enginePowerKw = base.enginePowerKw,
            peakTorqueNm = base.peakTorqueNm,
            fuelCapacityL = base.fuelCapacityL,
            fuelConsumptionBase = base.fuelConsumptionL100km,
            currentFuelL = vehicle.fuelLevelL,
            engineHealthPct = vehicle.engineHealthPct,
            tyreConditionPct = vehicle.tyreConditionPct
        )
    }
}