package com.transportsim.domain.models

data class Mission(
    val missionId: String,
    val title: String,
    val description: String,
    val type: MissionType,
    val rewardKsh: Int,
    val xpReward: Int,
    val requirements: MissionRequirements,
    val progress: MissionProgress,
    val expiresAt: String?,          // ISO timestamp
    val status: MissionStatus
)

enum class MissionType { CARGO, PASSENGER, EXPRESS, VIP, TRAINING }

data class MissionRequirements(
    val requiredVehicleCategory: VehicleCategory?,
    val minLevel: Int,
    val timeLimitMinutes: Int,
    val cargoCapacityKg: Int?,
    val passengerCount: Int?
)

data class MissionProgress(
    val current: Int,
    val target: Int,
    val unit: String   // e.g. "trips", "kg", "passengers"
)

enum class MissionStatus { AVAILABLE, IN_PROGRESS, COMPLETED, FAILED, EXPIRED }