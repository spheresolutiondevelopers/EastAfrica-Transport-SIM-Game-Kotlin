package com.transportsim.data.repository

import com.transportsim.data.datasource.AssetDataSource
import com.transportsim.domain.models.Upgrade
import com.transportsim.domain.models.VehicleCatalogEntry
import com.transportsim.domain.repositories.CatalogRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogRepositoryImpl @Inject constructor(
    private val assetDataSource: AssetDataSource
) : CatalogRepository {

    private var vehicleCatalogCache: List<VehicleCatalogEntry>? = null
    private var upgradeDefsCache: List<Upgrade>? = null

    override suspend fun getVehicleCatalog(): List<VehicleCatalogEntry> {
        if (vehicleCatalogCache == null) {
            vehicleCatalogCache = assetDataSource.loadVehicleCatalog()
        }
        return vehicleCatalogCache ?: emptyList()
    }

    override suspend fun getVehicleSpec(typeId: String): VehicleCatalogEntry {
        val catalog = getVehicleCatalog()
        return catalog.find { it.id == typeId }
            ?: throw IllegalArgumentException("Vehicle type not found: $typeId")
    }

    override suspend fun getUpgradeDefinitions(): List<Upgrade> {
        if (upgradeDefsCache == null) {
            upgradeDefsCache = assetDataSource.loadUpgradeDefinitions()
        }
        return upgradeDefsCache ?: emptyList()
    }

    override suspend fun getUpgradeDefinition(upgradeId: String): Upgrade {
        val defs = getUpgradeDefinitions()
        return defs.find { it.upgradeId == upgradeId }
            ?: throw IllegalArgumentException("Upgrade not found: $upgradeId")
    }
}