package com.transportsim.data.repository

import com.transportsim.data.database.dao.DailyStatsDao
import com.transportsim.data.database.dao.EconomyLedgerDao
import com.transportsim.data.database.dao.PlayerProfileDao
import com.transportsim.data.database.entities.DailyStatsEntity
import com.transportsim.data.database.entities.EconomyLedgerEntity
import com.transportsim.domain.models.DailyReward
import com.transportsim.domain.models.DailyRewardTrack
import com.transportsim.domain.models.RewardType
import com.transportsim.domain.repositories.EconomyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EconomyRepositoryImpl @Inject constructor(
    private val profileDao: PlayerProfileDao,
    private val ledgerDao: EconomyLedgerDao,
    private val dailyStatsDao: DailyStatsDao
) : EconomyRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())

    override suspend fun getBalance(): Int {
        val profile = profileDao.getProfile() ?: return 0
        return profile.balanceKsh
    }

    override suspend fun addTransaction(amount: Int, type: String, description: String) {
        val profile = profileDao.getProfile() ?: throw IllegalStateException("No profile")
        val newBalance = profile.balanceKsh + amount
        val entry = EconomyLedgerEntity(
            txType = type,
            amountKsh = amount,
            balanceAfterKsh = newBalance,
            relatedId = null,
            description = description,
            createdAt = isoFormat.format(Date())
        )
        ledgerDao.insert(entry)
        profileDao.addBalanceAndXp(amount, 0)
    }

    override suspend fun getDailyRewards(): DailyRewardTrack {
        val today = dateFormat.format(Date())
        val rewards = listOf(
            DailyReward(0, "+500 KSH", "🪙", 500, RewardType.CURRENCY),
            DailyReward(1, "Free Fuel", "⛽", 1, RewardType.FUEL),
            DailyReward(2, "Route Unlock", "🎫", 1, RewardType.PART),
            DailyReward(3, "2× XP", "⭐", 2, RewardType.XP_MULTIPLIER),
            DailyReward(4, "+2000 KSH", "💰", 2000, RewardType.CURRENCY),
            DailyReward(5, "Rare Part", "🔧", 1, RewardType.PART),
            DailyReward(6, "Free Vehicle", "🚌", 1, RewardType.VEHICLE)
        )
        // In a real implementation, we'd track which step the player is on
        return DailyRewardTrack(
            date = today,
            currentStep = 0,
            rewards = rewards
        )
    }

    override suspend fun claimDailyReward(): Result<DailyReward> {
        return runCatching {
            val track = getDailyRewards()
            val step = track.currentStep
            if (step >= track.rewards.size) {
                throw IllegalStateException("All rewards claimed for today")
            }
            val reward = track.rewards[step]
            // Apply reward to player
            when (reward.type) {
                RewardType.CURRENCY -> {
                    profileDao.addBalanceAndXp(reward.amount, 0)
                }
                RewardType.XP_MULTIPLIER -> {
                    // Apply XP multiplier
                }
                else -> {
                    // Other rewards (parts, vehicles, fuel) would be handled via inventory
                }
            }
            reward
        }
    }

    override fun observeBalance(): Flow<Int> {
        return profileDao.observeProfile().map { profile ->
            profile?.balanceKsh ?: 0
        }
    }
}