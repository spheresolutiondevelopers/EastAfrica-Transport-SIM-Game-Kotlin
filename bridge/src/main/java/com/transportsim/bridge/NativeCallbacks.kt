package com.transportsim.bridge

import android.util.Log
import com.transportsim.bridge.models.VehiclePhysicsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles callbacks from native code back to Kotlin.
 * These methods are called from C++ via JNI.
 */
@Singleton
class NativeCallbacks @Inject constructor() {
    companion object {
        private const val TAG = "NativeCallbacks"
    }

    // Expose callback data via flows
    private val _onCollision = MutableStateFlow<CollisionData?>(null)
    private val _onStopReached = MutableStateFlow<StopReachedData?>(null)
    private val _onScoreUpdate = MutableStateFlow<Int>(0)
    private val _onRouteProgress = MutableStateFlow<Float>(0f)
    private val _onVehicleStateUpdate = MutableStateFlow<VehiclePhysicsState?>(null)

    // Public flows for UI observation
    val collisionEvents = _onCollision.asStateFlow()
    val stopReachedEvents = _onStopReached.asStateFlow()
    val scoreUpdates = _onScoreUpdate.asStateFlow()
    val routeProgress = _onRouteProgress.asStateFlow()
    val vehicleStateUpdates = _onVehicleStateUpdate.asStateFlow()

    /**
     * Called from native code when a collision occurs.
     * @param severity 0=minor, 1=moderate, 2=severe
     */
    @Suppress("unused") // Called from JNI
    fun onCollision(severity: Int, objectId: Int, damage: Float) {
        Log.d(TAG, "Collision: severity=$severity, objectId=$objectId, damage=$damage")
        _onCollision.value = CollisionData(severity, objectId, damage)
    }

    /**
     * Called from native code when the vehicle reaches a bus stop.
     */
    @Suppress("unused") // Called from JNI
    fun onStopReached(stopId: Int, stopName: String, passengers: Int) {
        Log.d(TAG, "Stop reached: $stopName, passengers=$passengers")
        _onStopReached.value = StopReachedData(stopId, stopName, passengers)
    }

    /**
     * Called from native code when the score updates.
     */
    @Suppress("unused") // Called from JNI
    fun onScoreUpdate(score: Int) {
        _onScoreUpdate.value = score
    }

    /**
     * Called from native code when the route progress updates.
     * progress is 0.0 to 1.0
     */
    @Suppress("unused") // Called from JNI
    fun onRouteProgress(progress: Float) {
        _onRouteProgress.value = progress
    }

    /**
     * Called from native code with the latest vehicle state.
     * This can be used for real-time HUD updates without polling.
     */
    @Suppress("unused") // Called from JNI
    fun onVehicleStateUpdate(stateJson: String) {
        // This would parse the JSON and update the flow
        // For now, just log
        Log.v(TAG, "Vehicle state update received (length=${stateJson.length})")
    }

    /**
     * Called from native code when a mission objective is completed.
     */
    @Suppress("unused") // Called from JNI
    fun onMissionProgress(missionId: String, progress: Int, target: Int) {
        Log.d(TAG, "Mission progress: $missionId $progress/$target")
    }

    /**
     * Called from native code when the fuel level drops below a threshold.
     */
    @Suppress("unused") // Called from JNI
    fun onLowFuel(fuelPercent: Float) {
        Log.w(TAG, "Low fuel: $fuelPercent%")
    }

    /**
     * Called from native code when the vehicle overspeeds.
     */
    @Suppress("unused") // Called from JNI
    fun onOverspeed(currentSpeed: Float, limit: Float) {
        Log.w(TAG, "Overspeed: $currentSpeed km/h (limit $limit km/h)")
    }

    // ─── Clear methods ────────────────────────────────────────

    fun clearCollision() { _onCollision.value = null }
    fun clearStopReached() { _onStopReached.value = null }
}

// ─── Data classes for callbacks ──────────────────────────────

data class CollisionData(
    val severity: Int,
    val objectId: Int,
    val damage: Float
)

data class StopReachedData(
    val stopId: Int,
    val stopName: String,
    val passengers: Int
)