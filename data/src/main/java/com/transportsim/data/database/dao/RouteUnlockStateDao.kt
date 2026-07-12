package com.transportsim.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.transportsim.data.database.entities.RouteUnlockStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteUnlockStateDao {
    @Query("SELECT * FROM route_unlock_state WHERE routeId = :routeId")
    suspend fun getByRouteId(routeId: String): RouteUnlockStateEntity?

    @Query("SELECT * FROM route_unlock_state")
    suspend fun getAll(): List<RouteUnlockStateEntity>

    @Query("SELECT * FROM route_unlock_state")
    fun observeAll(): Flow<List<RouteUnlockStateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(state: RouteUnlockStateEntity)

    @Update
    suspend fun update(state: RouteUnlockStateEntity)

    @Query("UPDATE route_unlock_state SET isUnlocked = 1, stars = :stars, bestScore = :bestScore WHERE routeId = :routeId")
    suspend fun unlock(routeId: String, stars: Int, bestScore: Int)

    @Query("UPDATE route_unlock_state SET timesCompleted = timesCompleted + 1, bestScore = MAX(bestScore, :score) WHERE routeId = :routeId")
    suspend fun recordCompletion(routeId: String, score: Int)
}