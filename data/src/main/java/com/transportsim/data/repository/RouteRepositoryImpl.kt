package com.transportsim.data.repository

import com.transportsim.data.database.dao.RouteUnlockStateDao
import com.transportsim.data.database.dao.RouteWaypointDao
import com.transportsim.data.database.dao.TerrainChunkMetadataDao
import com.transportsim.data.datasource.WorldDatabaseDataSource
import com.transportsim.domain.models.Route
import com.transportsim.domain.models.RouteWaypoint
import com.transportsim.domain.models.TerrainChunk
import com.transportsim.domain.repositories.RouteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteRepositoryImpl @Inject constructor(
    private val worldDataSource: WorldDatabaseDataSource,
    private val unlockStateDao: RouteUnlockStateDao,
    private val waypointDao: RouteWaypointDao,
    private val terrainMetadataDao: TerrainChunkMetadataDao
) : RouteRepository {

    // In a real implementation, we'd parse the raw data from worldDataSource
    // and map to domain models. For brevity, we'll return a hardcoded list.
    override suspend fun getAllRoutes(): List<Route> {
        // This would normally query world_assets.db
        // For demo, return empty list
        return emptyList()
    }

    override suspend fun getRoute(routeId: String): Route? {
        // Query from worldDataSource
        return null
    }

    override suspend fun getRouteWaypoints(routeId: String): List<RouteWaypoint> {
        val entities = waypointDao.getForRoute(routeId)
        return entities.map { entity ->
            RouteWaypoint(
                name = entity.name,
                distanceFromOriginKm = entity.distanceKm,
                lat = 0.0, // We'd need to store lat/lng as well
                lng = 0.0,
                worldX = entity.worldX,
                worldZ = entity.worldZ,
                isTerminal = false
            )
        }
    }

    override suspend fun getUnlockableRoutes(): List<Route> {
        // Return routes where isUnlocked = false
        return emptyList()
    }

    override suspend fun unlockRoute(routeId: String): Result<Unit> {
        return runCatching {
            unlockStateDao.unlock(routeId, 0, 0)
        }
    }

    override fun observeRoutes(): Flow<List<Route>> {
        // In real implementation, observe from DB
        return emptyList<Route>().let { flowOf(it) }
    }

    // Helper to convert waypoint entities
    private fun toDomain(entity: RouteWaypointEntity): RouteWaypoint {
        return RouteWaypoint(
            name = entity.name,
            distanceFromOriginKm = entity.distanceKm,
            lat = 0.0,
            lng = 0.0,
            worldX = entity.worldX,
            worldZ = entity.worldZ,
            isTerminal = false
        )
    }
}