package com.transportsim.data.repository

import com.transportsim.data.database.dao.RouteStatsDao
import com.transportsim.data.database.dao.RouteUnlockStateDao
import com.transportsim.data.database.dao.RouteWaypointDao
import com.transportsim.data.database.entities.RouteStatsEntity
import com.transportsim.data.datasource.AssetDataSource
import com.transportsim.domain.models.Route
import com.transportsim.domain.models.RouteStats
import com.transportsim.domain.repositories.RouteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteRepositoryImpl @Inject constructor(
    private val assetDataSource: AssetDataSource,
    private val statsDao: RouteStatsDao,
    private val unlockStateDao: RouteUnlockStateDao,
    private val waypointDao: RouteWaypointDao
) : RouteRepository {

    private var routeCache: List<Route>? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())

    override suspend fun getAllRoutes(): List<Route> {
        if (routeCache == null) {
            routeCache = assetDataSource.loadRoutes()
        }
        return routeCache ?: emptyList()
    }

    override suspend fun getRoute(routeId: String): Route? {
        return getAllRoutes().find { it.id == routeId }
    }

    override suspend fun getRouteStats(routeId: String): RouteStats? {
        val entity = statsDao.getByRouteId(routeId)
        return entity?.let { toDomain(it) }
    }

    override suspend fun getAllRouteStats(): List<RouteStats> {
        return statsDao.getAll().map { toDomain(it) }
    }

    override suspend fun getUnlockableRoutes(): List<Route> {
        val allRoutes = getAllRoutes()
        val unlockedIds = unlockStateDao.getAll().map { it.routeId }.toSet()
        return allRoutes.filter { it.id in unlockedIds }
    }

    override suspend fun unlockRoute(routeId: String): Result<Unit> {
        return runCatching {
            unlockStateDao.unlock(routeId, 0, 0)
        }
    }

    override suspend fun recordRouteCompletion(
        routeId: String,
        score: Int,
        distance: Double,
        revenue: Int,
        xp: Int,
        duration: Int
    ): Result<Unit> {
        return runCatching {
            val playedAt = dateFormat.format(Date())
            statsDao.recordCompletion(routeId, score, distance, revenue, xp, duration, playedAt)

            // Update unlock state with best score and stars
            val stars = when {
                score >= 90 -> 3
                score >= 70 -> 2
                score >= 50 -> 1
                else -> 0
            }
            unlockStateDao.unlock(routeId, stars, score)
        }
    }

    override fun observeRoutes(): Flow<List<Route>> {
        // In a real implementation, you might want to observe the routes from a database
        // that is synchronized with the JSON assets. For now, just returning a flow of the cache.
        return kotlinx.coroutines.flow.flow {
            emit(getAllRoutes())
        }
    }

    override fun observeRouteStats(routeId: String): Flow<RouteStats?> {
        return statsDao.observeByRouteId(routeId).map { entity ->
            entity?.let { toDomain(it) }
        }
    }

    private fun toDomain(entity: RouteStatsEntity): RouteStats {
        return RouteStats(
            routeId = entity.routeId,
            timesCompleted = entity.timesCompleted,
            bestScore = entity.bestScore,
            totalDistanceKm = entity.totalDistanceKm,
            totalRevenueKsh = entity.totalRevenueKsh,
            totalXP = entity.totalXP,
            fastestTimeSeconds = entity.fastestTimeSeconds,
            lastPlayedAt = entity.lastPlayedAt,
            stars = entity.stars
        )
    }
}