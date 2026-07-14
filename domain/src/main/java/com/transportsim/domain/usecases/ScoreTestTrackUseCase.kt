package com.transportsim.domain.usecases

import com.transportsim.domain.models.SimulationMetrics

class ScoreTestTrackUseCase {
    operator fun invoke(
        durationSeconds: Float,
        distanceKm: Float,
        speedLimitViolations: Int,
        collisions: Int,
        stopAccuracy: Float,          // 0..1
        passengerSatisfaction: Float // 0..1
    ): TrainingScore {
        // Scoring algorithm for training scenarios
        val speedBonus = (distanceKm / durationSeconds).coerceAtMost(1.5f) * 10
        val violationPenalty = speedLimitViolations.toFloat() * 5
        val collisionPenalty = collisions.toFloat() * 25
        val stopBonus = stopAccuracy * 15
        val satisfactionBonus = passengerSatisfaction * 10

        val rawScore = 50 + speedBonus + stopBonus + satisfactionBonus - violationPenalty - collisionPenalty
        val finalScore = rawScore.coerceIn(0, 100)

        val grade = when {
            finalScore >= 90 -> "A (Excellent)"
            finalScore >= 75 -> "B (Good)"
            finalScore >= 60 -> "C (Average)"
            finalScore >= 40 -> "D (Needs Improvement)"
            else -> "F (Fail)"
        }

        return TrainingScore(
            score = finalScore.toInt(),
            grade = grade,
            speedBonus = speedBonus.toInt(),
            violationPenalty = violationPenalty.toInt(),
            collisionPenalty = collisionPenalty.toInt(),
            stopBonus = stopBonus.toInt(),
            satisfactionBonus = satisfactionBonus.toInt()
        )
    }
}

data class TrainingScore(
    val score: Int,
    val grade: String,
    val speedBonus: Int,
    val violationPenalty: Int,
    val collisionPenalty: Int,
    val stopBonus: Int,
    val satisfactionBonus: Int
)