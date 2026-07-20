package com.transportsim.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.transportsim.data.database.entities.TrainingProgressEntity

@Dao
interface TrainingProgressDao {
    @Query("SELECT * FROM training_progress WHERE scenarioId = :scenarioId")
    suspend fun getByScenarioId(scenarioId: String): TrainingProgressEntity?

    @Query("SELECT * FROM training_progress")
    suspend fun getAll(): List<TrainingProgressEntity>

    @Query("SELECT * FROM training_progress WHERE isCompleted = 1")
    suspend fun getCompleted(): List<TrainingProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: TrainingProgressEntity)

    @Update
    suspend fun update(progress: TrainingProgressEntity)

    @Query("""
        UPDATE training_progress 
        SET isCompleted = 1,
            bestScore = MAX(bestScore, :score),
            timesCompleted = timesCompleted + 1,
            stars = :stars,
            lastPlayedAt = :playedAt
        WHERE scenarioId = :scenarioId
    """)
    suspend fun recordCompletion(
        scenarioId: String,
        score: Int,
        stars: Int,
        playedAt: String
    )

    @Query("UPDATE training_progress SET isUnlocked = 1 WHERE scenarioId = :scenarioId")
    suspend fun unlockScenario(scenarioId: String)
}
