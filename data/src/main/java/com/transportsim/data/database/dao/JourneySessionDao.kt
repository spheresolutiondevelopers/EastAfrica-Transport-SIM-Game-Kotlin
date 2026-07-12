package com.transportsim.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.transportsim.data.database.entities.JourneySessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JourneySessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: JourneySessionEntity): Long

    @Update
    suspend fun update(session: JourneySessionEntity)

    @Query("SELECT * FROM journey_sessions WHERE sessionId = :sessionId")
    suspend fun getById(sessionId: Int): JourneySessionEntity?

    @Query("SELECT * FROM journey_sessions WHERE status = 'ACTIVE' ORDER BY startedAt DESC LIMIT 1")
    suspend fun getActiveSession(): JourneySessionEntity?

    @Query("SELECT * FROM journey_sessions WHERE vehicleId = :vehicleId ORDER BY startedAt DESC")
    suspend fun getForVehicle(vehicleId: Int): List<JourneySessionEntity>

    @Query("SELECT * FROM journey_sessions ORDER BY startedAt DESC LIMIT 100")
    fun observeRecent(): Flow<List<JourneySessionEntity>>

    @Query("UPDATE journey_sessions SET status = 'COMPLETED', endedAt = :endedAt, durationSec = :durationSec, distanceKm = :distanceKm, score = :score, revenueKsh = :revenueKsh, xpEarned = :xpEarned, passengers = :passengers, fuelUsedL = :fuelUsedL WHERE sessionId = :sessionId")
    suspend fun completeSession(
        sessionId: Int,
        endedAt: String,
        durationSec: Int,
        distanceKm: Double,
        score: Int,
        revenueKsh: Int,
        xpEarned: Int,
        passengers: Int,
        fuelUsedL: Float
    )
}