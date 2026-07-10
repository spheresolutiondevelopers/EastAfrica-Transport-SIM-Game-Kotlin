package com.transportsim.domain.repositories

import com.transportsim.domain.models.VehicleCatalogEntry
import com.transportsim.domain.models.Upgrade

interface CatalogRepository {
    suspend fun getVehicleCatalog(): List<VehicleCatalogEntry>
    suspend fun getVehicleSpec(typeId: String): VehicleCatalogEntry
    suspend fun getUpgradeDefinitions(): List<Upgrade>
    suspend fun getUpgradeDefinition(upgradeId: String): Upgrade
}