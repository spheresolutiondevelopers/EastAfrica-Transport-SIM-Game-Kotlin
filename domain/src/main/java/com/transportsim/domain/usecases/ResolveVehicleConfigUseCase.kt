package com.transportsim.domain.usecases

import com.transportsim.domain.models.ResolvedVehicleConfig
import com.transportsim.domain.repositories.CatalogRepository
import com.transportsim.domain.repositories.PlayerRepository
import com.transportsim.domain.repositories.FleetRepository

class ResolveVehicleConfigUseCase(
    private val catalogRepository: CatalogRepository,
    private val playerRepository: PlayerRepository,
    private val fleetRepository: FleetRepository
) {
    suspend operator fun invoke(vehicleId: Int): ResolvedVehicleConfig {
        val vehicle = playerRepository.getPlayerVehicle(vehicleId)
        val base = catalogRepository.getVehicleSpec(vehicle.typeId)
        // In a full implementation we'd also fetch upgrades from a repository
        // and apply them. This is a simplified version.
        // For now, we assume no upgrades.
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
            pacejkaD = base.pacejka.d * (vehicle.tyreConditionPct / 100f),
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