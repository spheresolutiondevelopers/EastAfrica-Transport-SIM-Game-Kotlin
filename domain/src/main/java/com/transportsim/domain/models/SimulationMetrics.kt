package com.transportsim.domain.models

data class SimulationMetrics(
    val frameTimeUs: Long,
    val physicsStepUs: Long,
    val renderTimeUs: Long,
    val objectCount: Int,
    val drawCalls: Int
)