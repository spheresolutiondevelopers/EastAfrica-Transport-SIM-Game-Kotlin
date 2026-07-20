package com.transportsim.app.ui.dashboard.models

import com.transportsim.domain.models.Mission
import com.transportsim.domain.models.Vehicle
import com.transportsim.domain.models.VehicleCategory

data class DashboardUiState(
    val categories: List<CategoryProgress> = emptyList(),
    val vehicles: List<Vehicle> = emptyList(),
    val activeMissions: List<Mission> = emptyList(),
    val routeStatuses: List<RouteStatus> = emptyList(),
    val fuelAlerts: List<FuelAlert> = emptyList(),
    val playerLevel: Int = 1,
    val playerXp: Int = 0,
    val nextLevelXp: Int = 1000,
    val playerBalance: Int = 0,
    val todayRevenue: Int = 0,
    val todayPassengers: Int = 0,
    val todayCargo: Int = 0,
    val todayOnTimeRate: Float = 0f,
    val mapVehicles: List<MapVehicle> = emptyList(),
    val selectedRouteId: String? = null,
    val selectedVehicleId: Int? = null
)

data class CategoryProgress(
    val category: VehicleCategory,
    val owned: Int,
    val total: Int
) {
    val progress: Float = if (total > 0) owned.toFloat() / total else 0f
}

data class RouteStatus(
    val routeId: String,
    val name: String,
    val status: String
)

data class FuelAlert(
    val vehicleId: Int,
    val fuelPct: Float
)

data class MapVehicle(
    val id: String,
    val x: Float,
    val y: Float,
    val category: VehicleCategory
)
