package com.transportsim.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.transportsim.data.database.entities.RouteWaypointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteWaypointDao {
    @Query("SELECT * FROM route_waypoints WHERE routeId = :routeId ORDER BY stopOrder ASC")
    suspend fun getForRoute(routeId: String): List<RouteWaypointEntity>

    @Query("SELECT * FROM route_waypoints WHERE routeId = :routeId ORDER BY stopOrder ASC")
    fun observeForRoute(routeId: String): Flow<List<RouteWaypointEntity>>

    @Query("SELECT * FROM route_waypoints WHERE routeId = :routeId AND stopOrder = :stopOrder")
    suspend fun getByStopOrder(routeId: String, stopOrder: Int): RouteWaypointEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(waypoint: RouteWaypointEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(waypoints: List<RouteWaypointEntity>)

    @Query("DELETE FROM route_waypoints WHERE routeId = :routeId")
    suspend fun deleteForRoute(routeId: String)

    @Query("DELETE FROM route_waypoints WHERE routeId = :routeId AND stopOrder >= :fromOrder")
    suspend fun deleteFromStopOrder(routeId: String, fromOrder: Int)
}