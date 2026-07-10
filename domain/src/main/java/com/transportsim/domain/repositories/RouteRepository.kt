package com.transportsim.domain.repositories

import com.transportsim.domain.models.Route
import com.transportsim.domain.models.RouteWaypoint
import com.transportsim.domain.models.TerrainChunk
import kotlinx.coroutines.flow.Flow

interface RouteRepository {
    suspend fun getAllRoutes(): List<Route>
    suspend fun getRoute(routeId: String): Route?
    suspend fun getRouteWaypoints(routeId: String): List<RouteWaypoint>
    suspend fun getUnlockableRoutes(): List<Route>
    suspend fun unlockRoute(routeId: String): Result<Unit>
    fun observeRoutes(): Flow<List<Route>>
}