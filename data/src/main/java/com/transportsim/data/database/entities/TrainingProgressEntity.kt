package com.transportsim.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "training_progress")
data class TrainingProgressEntity(
    @PrimaryKey
    val scenarioId: String,
    val isUnlocked: Boolean = false,
    val isCompleted: Boolean = false,
    val bestScore: Int = 0,
    val timesCompleted: Int = 0,
    val stars: Int = 0,
    val lastPlayedAt: String? = null,
    val unlockLevel: Int = 1
)
