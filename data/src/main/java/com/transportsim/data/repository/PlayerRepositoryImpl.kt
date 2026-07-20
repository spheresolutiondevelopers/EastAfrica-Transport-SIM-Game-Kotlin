package com.transportsim.data.repository

import com.transportsim.data.database.dao.PlayerProfileDao
import com.transportsim.data.database.dao.PlayerVehicleDao
import com.transportsim.data.database.dao.VehicleUpgradeDao
import com.transportsim.data.database.entities.PlayerProfileEntity
import com.transportsim.data.database.entities.PlayerVehicleEntity
import com.transportsim.data.database.entities.VehicleUpgradeEntity
import com.transportsim.domain.models.PlayerProfile
import com.transportsim.domain.models.Vehicle
import com.transportsim.domain.models.VehicleStatus
import com.transportsim.domain.models.VehicleUpgrade
import com.transportsim.domain.repositories.CatalogRepository
import com.transportsim.domain.repositories.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepositoryImpl @Inject constructor(
    private val profileDao: PlayerProfileDao,
    private val vehicleDao: PlayerVehicleDao,
    private val upgradeDao: VehicleUpgradeDao,
    private val catalogRepository: CatalogRepository
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

    override suspend fun ensureDefaultVehicle(): Vehicle {
        val vehicles = getPlayerVehicles()
        if (vehicles.isEmpty()) {
            val catalog = catalogRepository.getVehicleCatalog()
            val pickup = catalog.find { it.id == "pickup_single_cab" }
                ?: throw IllegalStateException("Default pickup not found in catalog")
            
            val serialNumber = "KE-${System.currentTimeMillis()}"
            val entity = PlayerVehicleEntity(
                typeId = pickup.id,
                serialNumber = serialNumber,
                displayName = "My Pickup",
                status = "IDLE",
                fuelLevelL = pickup.fuelCapacityL * 0.5f,
                fuelLevelPct = 50f,
                conditionPct = 100f,
                engineHealthPct = 100f,
                tyreConditionPct = 100f,
                odometerKm = 0f,
                currentRouteId = null,
                purchasedAt = System.currentTimeMillis().toString(),
                lastMaintainedAt = null,
                isFavorite = 1
            )
            val id = vehicleDao.insert(entity)
            val inserted = vehicleDao.getById(id.toInt())
                ?: throw IllegalStateException("Failed to insert default vehicle")
            
            // Seed upgrades
            val upgradeDefs = catalogRepository.getUpgradeDefinitions()
            upgradeDefs.forEach { def ->
                upgradeDao.insertOrUpdate(
                    VehicleUpgradeEntity(
                        vehicleId = inserted.vehicleId,
                        upgradeId = def.upgradeId,
                        currentLevel = 0
                    )
                )
            }
            return toDomain(inserted)
        }
        return vehicles.first()
    }

    override suspend fun getUpgradesForVehicle(vehicleId: Int): List<VehicleUpgrade> {
        val entities = upgradeDao.getForVehicle(vehicleId)
        return entities.map { entity ->
            VehicleUpgrade(
                vehicleId = entity.vehicleId,
                upgradeId = entity.upgradeId,
                currentLevel = entity.currentLevel
            )
        }
    }

    override suspend fun applyUpgrade(vehicleId: Int, upgradeId: String): Result<Unit> {
        return runCatching {
            val upgrade = upgradeDao.get(vehicleId, upgradeId)
                ?: throw IllegalArgumentException("Upgrade not found")
            
            val definitions = catalogRepository.getUpgradeDefinitions()
            val def = definitions.find { it.upgradeId == upgradeId }
                ?: throw IllegalArgumentException("Upgrade definition not found")
            
            if (upgrade.currentLevel >= def.maxLevel) {
                throw IllegalStateException("Upgrade already at max level")
            }
            
            val newLevel = upgrade.currentLevel + 1
            
            // Update upgrade level
            upgradeDao.insertOrUpdate(
                VehicleUpgradeEntity(
                    vehicleId = vehicleId,
                    upgradeId = upgradeId,
                    currentLevel = newLevel
                )
            )
        }
    }

    override suspend fun fullServiceVehicle(vehicleId: Int): Result<Unit> {
        return runCatching {
            val vehicle = vehicleDao.getById(vehicleId)
                ?: throw IllegalArgumentException("Vehicle not found")
            
            vehicleDao.update(
                vehicle.copy(
                    conditionPct = 100f,
                    engineHealthPct = 100f,
                    tyreConditionPct = 100f,
                    lastMaintainedAt = System.currentTimeMillis().toString()
                )
            )
        }
    }
}
