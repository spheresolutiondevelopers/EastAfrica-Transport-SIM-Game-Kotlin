package com.transportsim.data.repository

import com.transportsim.data.database.dao.PlayerVehicleDao
import com.transportsim.data.database.dao.VehicleUpgradeDao
import com.transportsim.data.database.entities.PlayerVehicleEntity
import com.transportsim.data.database.entities.VehicleUpgradeEntity
import com.transportsim.domain.models.Vehicle
import com.transportsim.domain.models.VehicleCategory
import com.transportsim.domain.models.VehicleStatus
import com.transportsim.domain.repositories.CatalogRepository
import com.transportsim.domain.repositories.FleetRepository
import com.transportsim.domain.repositories.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FleetRepositoryImpl @Inject constructor(
    private val vehicleDao: PlayerVehicleDao,
    private val upgradeDao: VehicleUpgradeDao,
    private val catalogRepository: CatalogRepository,
    private val playerRepository: PlayerRepository
) : FleetRepository {

    private fun toDomain(entity: PlayerVehicleEntity): Vehicle {
        // Convert entity to domain (see PlayerRepositoryImpl for full mapping)
        return Vehicle(
            vehicleId = entity.vehicleId,
            typeId = entity.typeId,
            serialNumber = entity.serialNumber,
            displayName = entity.displayName,
            status = VehicleStatus.valueOf(entity.status),
            fuelLevelL = entity.fuelLevelL,
            fuelLevelPct = entity.fuelLevelPct,
            conditionPct = entity.conditionPct,
            engineHealthPct = entity.engineHealthPct,
            tyreConditionPct = entity.tyreConditionPct,
            odometerKm = entity.odometerKm,
            currentRouteId = entity.currentRouteId,
            purchasedAt = entity.purchasedAt,
            lastMaintainedAt = entity.lastMaintainedAt,
            isFavorite = entity.isFavorite == 1
        )
    }

    override suspend fun getVehiclesByCategory(category: VehicleCategory): List<Vehicle> {
        val prefix = category.name.lowercase()
        return vehicleDao.getAll()
            .filter { it.typeId.startsWith(prefix) }
            .map { toDomain(it) }
    }

    override suspend fun getOwnedVehicles(): List<Vehicle> {
        return vehicleDao.getAll().map { toDomain(it) }
    }

    override suspend fun getActiveVehicles(): List<Vehicle> {
        return vehicleDao.getActiveVehicles().map { toDomain(it) }
    }

    override suspend fun purchaseVehicle(typeId: String): Result<Vehicle> {
        return runCatching {
            val catalog = catalogRepository.getVehicleCatalog()
            val spec = catalog.find { it.id == typeId }
                ?: throw IllegalArgumentException("Vehicle type not found: $typeId")

            // Create a new vehicle entity
            val serialNumber = "KE-${System.currentTimeMillis()}"
            val entity = PlayerVehicleEntity(
                typeId = typeId,
                serialNumber = serialNumber,
                displayName = spec.displayName,
                status = "IDLE",
                fuelLevelL = spec.fuelCapacityL * 0.3f, // Start with 30% fuel
                fuelLevelPct = 30f,
                conditionPct = 100f,
                engineHealthPct = 100f,
                tyreConditionPct = 100f,
                odometerKm = 0f,
                currentRouteId = null,
                purchasedAt = System.currentTimeMillis().toString(),
                lastMaintainedAt = null,
                isFavorite = 0
            )
            val id = vehicleDao.insert(entity)
            val insertedEntity = vehicleDao.getById(id.toInt())
                ?: throw IllegalStateException("Failed to insert vehicle")

            // Seed upgrade rows at level 0 for all upgrade slots
            val upgradeDefs = catalogRepository.getUpgradeDefinitions()
            upgradeDefs.forEach { upgradeDef ->
                upgradeDao.insertOrUpdate(
                    VehicleUpgradeEntity(
                        vehicleId = insertedEntity.vehicleId,
                        upgradeId = upgradeDef.upgradeId,
                        currentLevel = 0
                    )
                )
            }

            toDomain(insertedEntity)
        }
    }

    override suspend fun sellVehicle(vehicleId: Int): Result<Unit> {
        return runCatching {
            val vehicle = vehicleDao.getById(vehicleId)
                ?: throw IllegalArgumentException("Vehicle not found")
            if (vehicle.status == "ACTIVE") {
                throw IllegalStateException("Cannot sell an active vehicle")
            }
            // Delete upgrades first
            upgradeDao.deleteForVehicle(vehicleId)
            // Delete vehicle
            vehicleDao.delete(vehicleId)
        }
    }

    override fun observeFleet(): Flow<List<Vehicle>> {
        return vehicleDao.observeAll().map { entities ->
            entities.map { toDomain(it) }
        }
    }
}