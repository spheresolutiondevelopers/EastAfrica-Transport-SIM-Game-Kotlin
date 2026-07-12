package com.transportsim.bridge.models

import com.transportsim.bridge.annotations.Keep

@Keep
data class BridgeSimulationMetrics(
    val frameTimeUs: Long,
    val physicsStepUs: Long,
    val renderTimeUs: Long,
    val objectCount: Int,
    val drawCalls: Int,
    val triangles: Int,
    val fps: Float
)