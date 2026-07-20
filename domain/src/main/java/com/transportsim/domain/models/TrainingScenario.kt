package com.transportsim.domain.models

import kotlinx.serialization.Serializable

enum class TrainingDifficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}

@Serializable
data class TrainingScenario(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val difficulty: TrainingDifficulty,
    val durationMinutes: Int,
    val xpReward: Int,
    val unlockLevel: Int
)

data class TrainingScenarioWithProgress(
    val scenario: TrainingScenario,
    val progress: TrainingProgress
) {
    val isUnlocked: Boolean get() = progress.isUnlocked
    val isCompleted: Boolean get() = progress.isCompleted
    val bestScore: Int get() = progress.bestScore
    val timesCompleted: Int get() = progress.timesCompleted
    val stars: Int get() = progress.stars
}

data class TrainingProgress(
    val scenarioId: String,
    val isUnlocked: Boolean = false,
    val isCompleted: Boolean = false,
    val bestScore: Int = 0,
    val timesCompleted: Int = 0,
    val stars: Int = 0,
    val lastPlayedAt: String? = null,
    val unlockLevel: Int = 1
)

data class TrainingResult(
    val scenarioId: String,
    val score: Int,
    val grade: String,
    val xpEarned: Int,
    val details: TrainingScoreDetails
)

data class TrainingScoreDetails(
    val speedBonus: Int,
    val violationPenalty: Int,
    val collisionPenalty: Int,
    val stopBonus: Int,
    val satisfactionBonus: Int
)
