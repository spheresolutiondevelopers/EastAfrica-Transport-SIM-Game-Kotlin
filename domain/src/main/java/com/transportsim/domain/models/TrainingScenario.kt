package com.transportsim.domain.models

enum class TrainingScenarioDifficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}

data class TrainingScenario(
    val id: String,
    val title: String,
    val description: String,
    val difficulty: TrainingScenarioDifficulty,
    val icon: String, // Emoji or resource
    val durationMinutes: Int,
    val xpReward: Int,
    val unlockLevel: Int,
    val isUnlocked: Boolean = false,
    val isCompleted: Boolean = false,
    val bestScore: Int? = null
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