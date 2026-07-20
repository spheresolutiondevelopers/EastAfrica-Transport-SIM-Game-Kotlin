package com.transportsim.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.transportsim.data.database.entities.RouteStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteStatsDao {
    @Query("SELECT * FROM route_stats WHERE routeId = :routeId")
    suspend fun getByRouteId(routeId: String): RouteStatsEntity?

    @Query("SELECT * FROM route_stats WHERE routeId = :routeId")
    fun observeByRouteId(routeId: String): Flow<RouteStatsEntity?>

    @Query("SELECT * FROM route_stats")
    suspend fun getAll(): List<RouteStatsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stats: RouteStatsEntity)

    @Update
    suspend fun update(stats: RouteStatsEntity)

    @Query("""
        UPDATE route_stats 
        SET timesCompleted = timesCompleted + 1,
            bestScore = MAX(bestScore, :score),
            totalDistanceKm = totalDistanceKm + :distance,
            totalRevenueKsh = totalRevenueKsh + :revenue,
            totalXP = totalXP + :xp,
            fastestTimeSeconds = MIN(fastestTimeSeconds, :duration),
            lastPlayedAt = :playedAt
        WHERE routeId = :routeId
    """)
    suspend fun recordCompletion(
        routeId: String,
        score: Int,
        distance: Double,
        revenue: Int,
        xp: Int,
        duration: Int,
        playedAt: String
    )
}