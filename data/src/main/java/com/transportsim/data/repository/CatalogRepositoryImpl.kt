package com.transportsim.data.repository

import com.transportsim.data.datasource.AssetDataSource
import com.transportsim.domain.models.UpgradeDefinition
import com.transportsim.domain.models.VehicleCatalogEntry
import com.transportsim.domain.repositories.CatalogRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogRepositoryImpl @Inject constructor(
    private val assetDataSource: AssetDataSource
) : CatalogRepository {

    private var vehicleCatalogCache: List<VehicleCatalogEntry>? = null
    private var upgradeDefsCache: List<UpgradeDefinition>? = null

    override suspend fun getVehicleCatalog(): List<VehicleCatalogEntry> {
        if (vehicleCatalogCache == null) {
            vehicleCatalogCache = assetDataSource.loadVehicleCatalog()
        }
        if (vehicleCatalogCache.isNullOrEmpty()) {
             // Fallback for debugging if assets fail to load
             return listOf(
                 VehicleCatalogEntry(
                     id = "debug_bus",
                     displayName = "Debug Bus (Assets Failed)",
                     emoji = "🚌",
                     category = com.transportsim.domain.models.VehicleCategory.BUS,
                     massKg = 10000f,
                     maxPayloadKg = 5000f,
                     passengerCapacity = 50,
                     cargoCapacityKg = 0f,
                     wheelbaseM = 5f,
                     trackWidthM = 2f,
                     comHeightLadenM = 1f,
                     momentInertiaIzz = 100000f,
                     maxSpeedKph = 80f,
                     peakAccelMs2 = 1f,
                     emergencyBrakeMs2 = 5f,
                     minTurnRadiusM = 10f,
                     aeroDragCd = 0.5f,
                     frontalAreaM2 = 5f,
                     enginePowerKw = 150f,
                     peakTorqueNm = 800f,
                     peakTorqueRpm = 1500,
                     fuelCapacityL = 200f,
                     fuelConsumptionL100km = 20f,
                     pacejka = com.transportsim.domain.models.PacejkaCoeffs(1f, 1f, 1f, 1f),
                     suspension = com.transportsim.domain.models.SuspensionSpec(1f, 1f, 1f, 1f),
                     assetFileBase = "bus.glb",
                     purchaseCostKsh = 100000,
                     unlockLevel = 1
                 )
             )
        }
        return vehicleCatalogCache ?: emptyList()
    }

    override suspend fun getVehicleSpec(typeId: String): VehicleCatalogEntry {
        val catalog = getVehicleCatalog()
        return catalog.find { it.id == typeId }
            ?: throw IllegalArgumentException("Vehicle type not found: $typeId")
    }

    override suspend fun getUpgradeDefinitions(): List<UpgradeDefinition> {
        if (upgradeDefsCache == null) {
            upgradeDefsCache = assetDataSource.loadUpgradeDefinitions()
        }
        return upgradeDefsCache ?: emptyList()
    }

    override suspend fun getUpgradeDefinition(upgradeId: String): UpgradeDefinition {
        val defs = getUpgradeDefinitions()
        return defs.find { it.upgradeId == upgradeId }
            ?: throw IllegalArgumentException("Upgrade not found: $upgradeId")
    }
}
