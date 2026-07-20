package com.transportsim.domain.repositories

import com.transportsim.domain.models.VehicleCatalogEntry
import com.transportsim.domain.models.UpgradeDefinition

interface CatalogRepository {
    suspend fun getVehicleCatalog(): List<VehicleCatalogEntry>
    suspend fun getVehicleSpec(typeId: String): VehicleCatalogEntry
    suspend fun getUpgradeDefinitions(): List<UpgradeDefinition>
    suspend fun getUpgradeDefinition(upgradeId: String): UpgradeDefinition
}
