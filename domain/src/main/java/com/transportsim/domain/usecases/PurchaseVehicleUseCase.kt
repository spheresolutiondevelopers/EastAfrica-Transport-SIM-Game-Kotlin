package com.transportsim.domain.usecases

import com.transportsim.domain.models.Vehicle
import com.transportsim.domain.repositories.CatalogRepository
import com.transportsim.domain.repositories.EconomyRepository
import com.transportsim.domain.repositories.FleetRepository
import com.transportsim.domain.repositories.PlayerRepository

class PurchaseVehicleUseCase(
    private val catalogRepository: CatalogRepository,
    private val fleetRepository: FleetRepository,
    private val playerRepository: PlayerRepository,
    private val economyRepository: EconomyRepository
) {
    suspend operator fun invoke(typeId: String): Result<Vehicle> {
        return runCatching {
            val spec = catalogRepository.getVehicleSpec(typeId)
            val balance = economyRepository.getBalance()
            if (balance < spec.purchaseCostKsh) {
                throw IllegalArgumentException("Insufficient balance")
            }
            
            // Purchase the vehicle via fleet repository
            val vehicle = fleetRepository.purchaseVehicle(typeId).getOrThrow()
            
            // Deduct balance and record transaction
            economyRepository.addTransaction(-spec.purchaseCostKsh, "VEHICLE_PURCHASE", "Purchased $typeId")
            
            vehicle
        }
    }
}