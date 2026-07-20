package com.transportsim.domain.repositories

import com.transportsim.domain.models.Route
import com.transportsim.domain.models.RouteStats
import kotlinx.coroutines.flow.Flow

interface RouteRepository {
    suspend fun getAllRoutes(): List<Route>
    suspend fun getRoute(routeId: String): Route?
    suspend fun getRouteStats(routeId: String): RouteStats?
    suspend fun getAllRouteStats(): List<RouteStats>
    suspend fun getUnlockableRoutes(): List<Route>
    suspend fun unlockRoute(routeId: String): Result<Unit>
    suspend fun recordRouteCompletion(
        routeId: String,
        score: Int,
        distance: Double,
        revenue: Int,
        xp: Int,
        duration: Int
    ): Result<Unit>
    fun observeRoutes(): Flow<List<Route>>
    fun observeRouteStats(routeId: String): Flow<RouteStats?>
}