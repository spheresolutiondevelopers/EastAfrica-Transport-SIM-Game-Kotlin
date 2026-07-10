package com.transportsim.domain.repositories

import com.transportsim.domain.models.Vehicle
import com.transportsim.domain.models.VehicleCategory
import kotlinx.coroutines.flow.Flow

interface FleetRepository {
    suspend fun getVehiclesByCategory(category: VehicleCategory): List<Vehicle>
    suspend fun getOwnedVehicles(): List<Vehicle>
    suspend fun getActiveVehicles(): List<Vehicle>
    suspend fun purchaseVehicle(typeId: String): Result<Vehicle>
    suspend fun sellVehicle(vehicleId: Int): Result<Unit>
    fun observeFleet(): Flow<List<Vehicle>>
}