package com.transportsim.data.repository

import com.transportsim.data.database.dao.PlayerProfileDao
import com.transportsim.data.database.dao.PlayerVehicleDao
import com.transportsim.data.database.entities.PlayerProfileEntity
import com.transportsim.data.database.entities.PlayerVehicleEntity
import com.transportsim.domain.models.PlayerProfile
import com.transportsim.domain.models.Vehicle
import com.transportsim.domain.models.VehicleStatus
import com.transportsim.domain.repositories.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepositoryImpl @Inject constructor(
    private val profileDao: PlayerProfileDao,
    private val vehicleDao: PlayerVehicleDao
) : PlayerRepository {

    private fun toDomain(entity: PlayerProfileEntity): PlayerProfile = with(entity) {
        PlayerProfile(
            playerId = playerId,
            level = level,
            xp = xp,
            balanceKsh = balanceKsh,
            totalDistanceKm = totalDistanceKm,
            totalPassengers = totalPassengers,
            totalRevenue = totalRevenue,
            lastPlayedAt = lastPlayedAt,
            createdAt = createdAt
        )
    }

    private fun toDomain(entity: PlayerVehicleEntity): Vehicle = with(entity) {
        Vehicle(
            vehicleId = vehicleId,
            typeId = typeId,
            serialNumber = serialNumber,
            displayName = displayName,
            status = VehicleStatus.valueOf(status),
            fuelLevelL = fuelLevelL,
            fuelLevelPct = fuelLevelPct,
            conditionPct = conditionPct,
            engineHealthPct = engineHealthPct,
            tyreConditionPct = tyreConditionPct,
            odometerKm = odometerKm,
            currentRouteId = currentRouteId,
            purchasedAt = purchasedAt,
            lastMaintainedAt = lastMaintainedAt,
            isFavorite = isFavorite == 1
        )
    }

    private fun toEntity(domain: PlayerProfile): PlayerProfileEntity = with(domain) {
        PlayerProfileEntity(
            playerId = playerId,
            level = level,
            xp = xp,
            balanceKsh = balanceKsh,
            totalDistanceKm = totalDistanceKm,
            totalPassengers = totalPassengers,
            totalRevenue = totalRevenue,
            lastPlayedAt = lastPlayedAt,
            createdAt = createdAt
        )
    }

    private fun toEntity(domain: Vehicle): PlayerVehicleEntity = with(domain) {
        PlayerVehicleEntity(
            vehicleId = vehicleId,
            typeId = typeId,
            serialNumber = serialNumber,
            displayName = displayName,
            status = status.name,
            fuelLevelL = fuelLevelL,
            fuelLevelPct = fuelLevelPct,
            conditionPct = conditionPct,
            engineHealthPct = engineHealthPct,
            tyreConditionPct = tyreConditionPct,
            odometerKm = odometerKm,
            currentRouteId = currentRouteId,
            purchasedAt = purchasedAt,
            lastMaintainedAt = lastMaintainedAt,
            isFavorite = if (isFavorite) 1 else 0
        )
    }

    override suspend fun getProfile(): PlayerProfile {
        val entity = profileDao.getProfile() ?: PlayerProfileEntity()
        return toDomain(entity)
    }

    override suspend fun updateProfile(profile: PlayerProfile) {
        profileDao.upsertProfile(toEntity(profile))
    }

    override suspend fun getPlayerVehicles(): List<Vehicle> {
        return vehicleDao.getAll().map { toDomain(it) }
    }

    override suspend fun getPlayerVehicle(vehicleId: Int): Vehicle {
        return toDomain(vehicleDao.getById(vehicleId) ?: throw IllegalArgumentException("Vehicle not found"))
    }

    override suspend fun updateVehicle(vehicle: Vehicle) {
        vehicleDao.update(toEntity(vehicle))
    }

    override suspend fun updateVehicleStatus(vehicleId: Int, status: VehicleStatus) {
        vehicleDao.updateStatus(vehicleId, status.name)
    }

    override fun observeProfile(): Flow<PlayerProfile> {
        return profileDao.observeProfile().map { entity ->
            toDomain(entity ?: PlayerProfileEntity())
        }
    }

    override fun observeVehicles(): Flow<List<Vehicle>> {
        return vehicleDao.observeAll().map { entities ->
            entities.map { toDomain(it) }
        }
    }
}