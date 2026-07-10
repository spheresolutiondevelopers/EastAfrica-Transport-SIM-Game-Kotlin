package com.transportsim.domain.models

data class TrafficLightState(
    val index: Int,
    val phase: TrafficLightPhase,
    val timerSeconds: Int
)

enum class TrafficLightPhase { GREEN, AMBER, RED }