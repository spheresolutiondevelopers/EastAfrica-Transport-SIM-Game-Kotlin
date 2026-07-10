package com.transportsim.domain.usecases

import com.transportsim.domain.repositories.EconomyRepository
import com.transportsim.domain.repositories.PlayerRepository
import kotlinx.coroutines.flow.first

class RefuelVehicleUseCase(
    private val playerRepository: PlayerRepository,
    private val economyRepository: EconomyRepository
) {
    suspend operator fun invoke(vehicleId: Int, targetFuelLiters: Float? = null): Result<RefuelResult> {
        return runCatching {
            val vehicle = playerRepository.getPlayerVehicle(vehicleId)
            val fuelCapacity = vehicle.fuelLevelL + (targetFuelLiters ?: 0f) // simplified; actual capacity from catalog

            // In a real implementation, we'd get the actual fuel capacity from the catalog
            val maxFuel = 200f // placeholder for a bus
            val currentFuel = vehicle.fuelLevelL
            val fuelNeeded = if (targetFuelLiters != null) {
                targetFuelLiters.coerceIn(0f, maxFuel - currentFuel)
            } else {
                maxFuel - currentFuel
            }

            if (fuelNeeded <= 0.5f) {
                throw IllegalStateException("Tank is already full")
            }

            val costPerLiter = 180 // KSH per liter
            val totalCost = (fuelNeeded * costPerLiter).toInt()

            val profile = playerRepository.getProfile()
            if (profile.balanceKsh < totalCost) {
                throw IllegalStateException("Insufficient balance. Required: KSH $totalCost")
            }

            // Update balance
            val updatedProfile = profile.copy(balanceKsh = profile.balanceKsh - totalCost)
            playerRepository.updateProfile(updatedProfile)
            economyRepository.addTransaction(-totalCost, "REFUEL", "Refueled vehicle $vehicleId")

            // Update vehicle fuel
            val updatedVehicle = vehicle.copy(
                fuelLevelL = currentFuel + fuelNeeded,
                fuelLevelPct = ((currentFuel + fuelNeeded) / maxFuel * 100).coerceAtMost(100f)
            )
            playerRepository.updateVehicle(updatedVehicle)

            RefuelResult(
                vehicleId = vehicleId,
                fuelAdded = fuelNeeded,
                totalCost = totalCost,
                newFuelLevel = updatedVehicle.fuelLevelL,
                newFuelPct = updatedVehicle.fuelLevelPct,
                newBalance = updatedProfile.balanceKsh
            )
        }
    }
}

data class RefuelResult(
    val vehicleId: Int,
    val fuelAdded: Float,
    val totalCost: Int,
    val newFuelLevel: Float,
    val newFuelPct: Float,
    val newBalance: Int
)