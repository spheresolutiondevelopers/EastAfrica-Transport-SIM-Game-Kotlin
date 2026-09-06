package com.transportsim.domain.repositories

import com.transportsim.domain.models.DailyReward
import com.transportsim.domain.models.DailyRewardTrack
import com.transportsim.domain.models.DailyPerformance
import kotlinx.coroutines.flow.Flow

interface EconomyRepository {
    suspend fun getBalance(): Int
    suspend fun addTransaction(amount: Int, type: String, description: String)
    suspend fun getDailyRewards(): DailyRewardTrack
    suspend fun claimDailyReward(): Result<DailyReward>
    fun observeBalance(): Flow<Int>
    fun observeTodayPerformance(): Flow<DailyPerformance>
}
