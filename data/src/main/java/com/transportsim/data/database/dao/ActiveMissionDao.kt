package com.transportsim.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.transportsim.data.database.entities.ActiveMissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActiveMissionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mission: ActiveMissionEntity)

    @Update
    suspend fun update(mission: ActiveMissionEntity)

    @Query("SELECT * FROM active_missions WHERE status = 'AVAILABLE'")
    suspend fun getAvailable(): List<ActiveMissionEntity>

    @Query("SELECT * FROM active_missions WHERE status = 'IN_PROGRESS'")
    suspend fun getInProgress(): List<ActiveMissionEntity>

    @Query("SELECT * FROM active_missions WHERE status = 'IN_PROGRESS'")
    fun observeInProgress(): Flow<List<ActiveMissionEntity>>

    @Query("SELECT * FROM active_missions WHERE missionId = :missionId")
    suspend fun getById(missionId: String): ActiveMissionEntity?

    @Query("UPDATE active_missions SET status = 'COMPLETED' WHERE missionId = :missionId")
    suspend fun markCompleted(missionId: String)

    @Query("UPDATE active_missions SET status = 'FAILED' WHERE missionId = :missionId")
    suspend fun markFailed(missionId: String)

    @Query("UPDATE active_missions SET progressCurrent = progressCurrent + :increment WHERE missionId = :missionId")
    suspend fun incrementProgress(missionId: String, increment: Int)

    @Query("DELETE FROM active_missions WHERE status = 'COMPLETED' OR status = 'FAILED'")
    suspend fun deleteCompletedOrFailed()

    @Query("DELETE FROM active_missions WHERE expiresAt < :expiryCutoff")
    suspend fun deleteExpired(expiryCutoff: String)
}