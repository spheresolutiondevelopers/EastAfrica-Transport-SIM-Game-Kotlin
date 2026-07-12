package com.transportsim.bridge.models

import com.transportsim.bridge.annotations.Keep

@Keep
data class NativeTrafficLightState(
    val index: Int,
    val state: Int,  // 0=red, 1=amber, 2=green
    val timerMs: Int
)