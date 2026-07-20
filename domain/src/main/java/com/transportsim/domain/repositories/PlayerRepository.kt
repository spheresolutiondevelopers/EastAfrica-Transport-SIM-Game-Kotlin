package com.transportsim.domain.repositories

import com.transportsim.domain.models.PlayerProfile
import com.transportsim.domain.models.Vehicle
import com.transportsim.domain.models.VehicleStatus
import com.transportsim.domain.models.VehicleUpgrade
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    suspend fun getProfile(): PlayerProfile
    suspend fun updateProfile(profile: PlayerProfile)
    suspend fun getPlayerVehicles(): List<Vehicle>
    suspend fun getPlayerVehicle(vehicleId: Int): Vehicle
    suspend fun updateVehicle(vehicle: Vehicle)
    suspend fun updateVehicleStatus(vehicleId: Int, status: VehicleStatus)
    fun observeProfile(): Flow<PlayerProfile>
    fun observeVehicles(): Flow<List<Vehicle>>

    suspend fun ensureDefaultVehicle(): Vehicle
    suspend fun getUpgradesForVehicle(vehicleId: Int): List<VehicleUpgrade>
    suspend fun applyUpgrade(vehicleId: Int, upgradeId: String): Result<Unit>
    suspend fun fullServiceVehicle(vehicleId: Int): Result<Unit>
}
