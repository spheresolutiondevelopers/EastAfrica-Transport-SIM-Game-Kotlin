package com.transportsim.domain.models

data class DailyRewardTrack(
    val date: String,          // yyyy-MM-dd
    val currentStep: Int,      // 0-based index
    val rewards: List<DailyReward>
)

data class DailyReward(
    val step: Int,
    val description: String,
    val icon: String,
    val amount: Int,           // currency or quantity
    val type: RewardType
)

enum class RewardType { CURRENCY, FUEL, PART, XP_MULTIPLIER, VEHICLE }