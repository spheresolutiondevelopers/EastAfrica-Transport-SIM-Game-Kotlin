package com.transportsim.domain.usecases

import com.transportsim.domain.models.RouteWaypoint
import com.transportsim.domain.repositories.RouteRepository

class GetRouteWaypointsUseCase(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(routeId: String): List<RouteWaypoint> =
        routeRepository.getRouteWaypoints(routeId)
}