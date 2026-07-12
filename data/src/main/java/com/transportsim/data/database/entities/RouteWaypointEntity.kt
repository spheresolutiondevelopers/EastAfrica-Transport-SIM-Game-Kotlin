package com.transportsim.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route_waypoints")
data class RouteWaypointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val routeId: String,
    val stopOrder: Int,
    val name: String,
    val worldX: Double,
    val worldZ: Double,
    val distanceKm: Double
)