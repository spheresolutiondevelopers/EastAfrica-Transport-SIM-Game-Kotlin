package com.transportsim.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.transportsim.data.database.entities.DailyStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyStatsDao {
    @Query("SELECT * FROM daily_stats WHERE statDate = :date")
    suspend fun getByDate(date: String): DailyStatsEntity?

    @Query("SELECT * FROM daily_stats WHERE statDate = :date")
    fun observeByDate(date: String): Flow<DailyStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(stats: DailyStatsEntity)

    @Query("SELECT * FROM daily_stats ORDER BY statDate DESC LIMIT 30")
    suspend fun getLast30Days(): List<DailyStatsEntity>

    @Query("SELECT SUM(revenueKsh) FROM daily_stats WHERE statDate >= :fromDate")
    suspend fun getRevenueSince(fromDate: String): Int?
}