package com.transportsim.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.transportsim.data.database.entities.StopEventEntity

@Dao
interface StopEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stopEvent: StopEventEntity): Long

    @Query("SELECT * FROM stop_events WHERE sessionId = :sessionId ORDER BY arrivalTime ASC")
    suspend fun getForSession(sessionId: Int): List<StopEventEntity>

    @Query("DELETE FROM stop_events WHERE sessionId = :sessionId")
    suspend fun deleteForSession(sessionId: Int)
}