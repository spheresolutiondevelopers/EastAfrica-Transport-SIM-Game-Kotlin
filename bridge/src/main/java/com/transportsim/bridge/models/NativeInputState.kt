package com.transportsim.bridge.models

import com.transportsim.bridge.annotations.Keep

/**
 * Input state passed from Kotlin to C++ every physics tick.
 * All fields are primitive for fast JNI transfer.
 */
@Keep
data class NativeInputState(
    val throttle: Float,    // 0..1
    val brake: Float,       // 0..1
    val steerAngle: Float,  // -35..35 degrees
    val handbrake: Boolean,
    val horn: Boolean,
    val timestampMs: Long
)