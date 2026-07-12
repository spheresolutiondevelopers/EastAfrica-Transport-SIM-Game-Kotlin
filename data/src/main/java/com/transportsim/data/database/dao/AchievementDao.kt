package com.transportsim.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.transportsim.data.database.entities.AchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(achievement: AchievementEntity)

    @Update
    suspend fun update(achievement: AchievementEntity)

    @Query("SELECT * FROM achievements")
    suspend fun getAll(): List<AchievementEntity>

    @Query("SELECT * FROM achievements")
    fun observeAll(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE isUnlocked = 0")
    suspend fun getLocked(): List<AchievementEntity>

    @Query("SELECT * FROM achievements WHERE achievementId = :id")
    suspend fun getById(id: String): AchievementEntity?

    @Query("UPDATE achievements SET currentProgress = :progress WHERE achievementId = :id")
    suspend fun updateProgress(id: String, progress: Int)

    @Query("UPDATE achievements SET isUnlocked = 1, unlockedAt = :unlockedAt WHERE achievementId = :id")
    suspend fun unlock(id: String, unlockedAt: String)
}