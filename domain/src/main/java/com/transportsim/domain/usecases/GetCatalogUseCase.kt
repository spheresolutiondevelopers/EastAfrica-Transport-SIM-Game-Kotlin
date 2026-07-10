package com.transportsim.domain.usecases

import com.transportsim.domain.models.VehicleCatalogEntry
import com.transportsim.domain.repositories.CatalogRepository
import com.transportsim.domain.repositories.PlayerRepository

class GetCatalogUseCase(
    private val catalogRepository: CatalogRepository,
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(): Map<VehicleCatalogEntry, Boolean> {
        val catalog = catalogRepository.getVehicleCatalog()
        val ownedIds = playerRepository.getPlayerVehicles().map { it.typeId }.toSet()
        return catalog.associateWith { it.id in ownedIds }
    }
}