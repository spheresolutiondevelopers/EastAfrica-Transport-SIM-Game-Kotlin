package com.transportsim.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val achievementId: String,
    val name: String,
    val category: String, // DISTANCE, PASSENGERS, REVENUE, SKILL
    val description: String?,
    val targetValue: Int,
    val currentProgress: Int = 0,
    val isUnlocked: Int = 0, // 0 = false, 1 = true
    val unlockedAt: String?
)