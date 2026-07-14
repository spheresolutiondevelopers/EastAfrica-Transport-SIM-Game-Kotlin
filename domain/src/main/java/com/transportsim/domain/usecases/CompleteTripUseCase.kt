package com.transportsim.domain.usecases

import com.transportsim.domain.repositories.EconomyRepository
import com.transportsim.domain.repositories.MissionRepository
import com.transportsim.domain.repositories.PlayerRepository

class CompleteTripUseCase(
    private val playerRepository: PlayerRepository,
    private val missionRepository: MissionRepository,
    private val economyRepository: EconomyRepository
) {
    suspend operator fun invoke(vehicleId: Int, distanceKm: Double, passengers: Int, score: Int): Result<TripResult> {
        return runCatching {
            // 1. Update vehicle odometer and condition
            val vehicle = playerRepository.getPlayerVehicle(vehicleId)
            val updatedVehicle = vehicle.copy(
                odometerKm = vehicle.odometerKm + distanceKm.toFloat(),
                conditionPct = (vehicle.conditionPct - 0.05f).coerceAtLeast(0f)
            )
            playerRepository.updateVehicle(updatedVehicle)

            // 2. Update player profile (XP, balance, etc.)
            val profile = playerRepository.getProfile()
            val revenue = (distanceKm * 50 + passengers * 20).toInt()
            val xpEarned = (distanceKm * 10 + passengers * 5 + score).toInt()
            val newBalance = profile.balanceKsh + revenue
            val newXp = profile.xp + xpEarned
            val newLevel = (newXp / 1000) + 1 // simple level formula

            val updatedProfile = profile.copy(
                balanceKsh = newBalance,
                xp = newXp,
                level = newLevel,
                totalDistanceKm = profile.totalDistanceKm + distanceKm,
                totalPassengers = profile.totalPassengers + passengers,
                totalRevenue = profile.totalRevenue + revenue
            )
            playerRepository.updateProfile(updatedProfile)

            // 3. Record economy transaction
            economyRepository.addTransaction(revenue, "JOURNEY_REVENUE", "Trip completed")

            // 4. Check mission progress (simplified: just log)
            // In real implementation, we'd update mission progress

            TripResult(
                revenue = revenue,
                xpEarned = xpEarned,
                newBalance = newBalance,
                newLevel = newLevel
            )
        }
    }
}

data class TripResult(
    val revenue: Int,
    val xpEarned: Int,
    val newBalance: Int,
    val newLevel: Int
)