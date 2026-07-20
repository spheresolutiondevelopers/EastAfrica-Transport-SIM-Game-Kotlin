package com.transportsim.bridge

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.transportsim.bridge.models.*
import com.transportsim.domain.models.ResolvedVehicleConfig
import com.transportsim.domain.models.RouteWaypoint
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class NativeEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        init {
            System.loadLibrary("transportsim_native")
        }

        /**
         * Initializes the C++ physics engine with vehicle config, road segments, and bus stops.
         * Returns a session handle that must be passed to all subsequent native calls.
         */
        @JvmStatic
        external fun nativeInitSession(
            configJson: String,
            segmentsJson: String,
            stopsJson: String
        ): Long

        /**
         * Steps the physics simulation forward by dt seconds.
         * Returns true if the session is still active (not complete).
         */
        @JvmStatic
        external fun nativeStepPhysics(sessionHandle: Long, dtSeconds: Float): Boolean

        /**
         * Gets the current vehicle state from the C++ engine.
         * Returns a JSON string that we parse on the Kotlin side.
         */
        @JvmStatic
        external fun nativeGetVehicleState(sessionHandle: Long): String

        /**
         * Gets the current traffic light states.
         */
        @JvmStatic
        external fun nativeGetTrafficLights(sessionHandle: Long): String

        /**
         * Gets the current simulation metrics.
         */
        @JvmStatic
        external fun nativeGetMetrics(sessionHandle: Long): String

        /**
         * Sets the vehicle input state.
         */
        @JvmStatic
        external fun nativeSetInput(
            sessionHandle: Long,
            throttle: Float,
            brake: Float,
            steerAngle: Float,
            handbrake: Boolean,
            horn: Boolean
        )

        /**
         * Ends the session and returns the final score.
         */
        @JvmStatic
        external fun nativeEndSession(sessionHandle: Long): Int

        @JvmStatic
        external fun nativeInitRenderer(
            surface: Any,  // Surface or SurfaceView
            width: Int,
            height: Int,
            assetManager: android.content.res.AssetManager
        )

        @JvmStatic
        external fun nativeRenderFrame(sessionHandle: Long): Boolean

        @JvmStatic
        external fun nativeSetCamera(
            eyeX: Float, eyeY: Float, eyeZ: Float,
            targetX: Float, targetY: Float, targetZ: Float
        )

        @JvmStatic
        external fun nativeResizeRenderer(width: Int, height: Int)

        @JvmStatic
        external fun nativeInjectChunk(
            sessionHandle: Long,
            chunkX: Int,
            chunkY: Int,
            data: ByteArray
        ): Boolean

        @JvmStatic
        external fun nativeEvictChunk(sessionHandle: Long, chunkX: Int, chunkY: Int): Boolean

        @JvmStatic
        external fun nativeGetFriction(
            sessionHandle: Long,
            worldX: Float,
            worldZ: Float,
            wet: Boolean
        ): Float
    }

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val _vehicleState = MutableStateFlow(VehiclePhysicsState.empty())
    val vehicleState: StateFlow<VehiclePhysicsState> = _vehicleState.asStateFlow()

    private val _metrics = MutableStateFlow(BridgeSimulationMetrics(0, 0, 0, 0, 0, 0, 0f))
    val metrics: StateFlow<BridgeSimulationMetrics> = _metrics.asStateFlow()

    private var sessionHandle: Long = 0L

    // ─── Helper methods (Kotlin side) ──────────────────────────

    private val vehicleStateAdapter: JsonAdapter<VehiclePhysicsState> =
        moshi.adapter(VehiclePhysicsState::class.java)

    private val trafficLightAdapter: JsonAdapter<List<NativeTrafficLightState>> =
        moshi.adapter(Types.newParameterizedType(List::class.java, NativeTrafficLightState::class.java))

    private val metricsAdapter: JsonAdapter<BridgeSimulationMetrics> =
        moshi.adapter(BridgeSimulationMetrics::class.java)

    private val configAdapter: JsonAdapter<NativeVehicleConfig> =
        moshi.adapter(NativeVehicleConfig::class.java)

    private val segmentAdapter: JsonAdapter<List<NativeRoadSegment>> =
        moshi.adapter(Types.newParameterizedType(List::class.java, NativeRoadSegment::class.java))

    private val stopAdapter: JsonAdapter<List<NativeBusStop>> =
        moshi.adapter(Types.newParameterizedType(List::class.java, NativeBusStop::class.java))

    /**
     * Initializes a simulation session from domain models.
     * This converts domain models to native flat structs and passes them to C++.
     */
    suspend fun initSession(
        vehicleConfig: ResolvedVehicleConfig,
        roadSegments: List<NativeRoadSegment>,
        busStops: List<NativeBusStop>
    ): Long {
        // Convert ResolvedVehicleConfig to NativeVehicleConfig
        val nativeConfig = NativeVehicleConfig(
            vehicleId = vehicleConfig.vehicleId,
            typeId = vehicleConfig.typeId,
            massKg = vehicleConfig.massKg,
            maxPayloadKg = vehicleConfig.maxPayloadKg,
            wheelbaseM = vehicleConfig.wheelbaseM,
            comHeightLadenM = vehicleConfig.comHeightLadenM,
            momentInertiaIzz = vehicleConfig.momentInertiaIzz,
            pacejkaB = vehicleConfig.pacejkaB,
            pacejkaC = vehicleConfig.pacejkaC,
            pacejkaD = vehicleConfig.pacejkaD,
            pacejkaE = vehicleConfig.pacejkaE,
            frontSpringRate = vehicleConfig.frontSpringRate,
            rearSpringRate = vehicleConfig.rearSpringRate,
            frontDamping = vehicleConfig.frontDamping,
            rearDamping = vehicleConfig.rearDamping,
            maxSpeedKph = vehicleConfig.maxSpeedKph,
            passengerCapacity = vehicleConfig.passengerCapacity,
            fuelEffMult = vehicleConfig.fuelEffMult,
            brakeMs2 = vehicleConfig.brakeMs2,
            navAccuracyPct = vehicleConfig.navAccuracyPct,
            dragCd = vehicleConfig.dragCd,
            frontalAreaM2 = vehicleConfig.frontalAreaM2,
            enginePowerKw = vehicleConfig.enginePowerKw,
            peakTorqueNm = vehicleConfig.peakTorqueNm,
            fuelCapacityL = vehicleConfig.fuelCapacityL,
            fuelConsumptionBase = vehicleConfig.fuelConsumptionBase,
            currentFuelL = vehicleConfig.currentFuelL,
            engineHealthPct = vehicleConfig.engineHealthPct,
            tyreConditionPct = vehicleConfig.tyreConditionPct
        )

        val configJson = configAdapter.toJson(nativeConfig)
        val segmentsJson = segmentAdapter.toJson(roadSegments)
        val stopsJson = stopAdapter.toJson(busStops)

        sessionHandle = nativeInitSession(configJson, segmentsJson, stopsJson)
        return sessionHandle
    }

    /**
     * Steps the physics simulation by 1ms (0.001 seconds).
     * This should be called 1000 times per second.
     */
    fun stepPhysics(dtSeconds: Float = 0.001f): Boolean {
        if (sessionHandle == 0L) return false
        return nativeStepPhysics(sessionHandle, dtSeconds)
    }

    /**
     * Updates the vehicle state from native and publishes it to the StateFlow.
     * This should be called after each physics step or at a lower frequency (e.g., 60 Hz).
     */
    fun updateVehicleState() {
        if (sessionHandle == 0L) return
        val json = nativeGetVehicleState(sessionHandle)
        val state = vehicleStateAdapter.fromJson(json) ?: VehiclePhysicsState.empty()
        _vehicleState.value = state
    }

    /**
     * Gets the traffic light states as a list.
     */
    fun getTrafficLights(): List<NativeTrafficLightState> {
        if (sessionHandle == 0L) return emptyList()
        val json = nativeGetTrafficLights(sessionHandle)
        return trafficLightAdapter.fromJson(json) ?: emptyList()
    }

    /**
     * Gets the current simulation metrics.
     */
    fun getMetrics(): BridgeSimulationMetrics {
        if (sessionHandle == 0L) return BridgeSimulationMetrics(0, 0, 0, 0, 0, 0, 0f)
        val json = nativeGetMetrics(sessionHandle)
        return metricsAdapter.fromJson(json) ?: BridgeSimulationMetrics(0, 0, 0, 0, 0, 0, 0f)
    }

    /**
     * Sets the vehicle input state.
     */
    fun setInput(input: NativeInputState) {
        if (sessionHandle == 0L) return
        nativeSetInput(
            sessionHandle,
            input.throttle,
            input.brake,
            input.steerAngle,
            input.handbrake,
            input.horn
        )
    }

    /**
     * Sets vehicle input from individual parameters.
     */
    fun setInput(
        throttle: Float = 0f,
        brake: Float = 0f,
        steerAngle: Float = 0f,
        handbrake: Boolean = false,
        horn: Boolean = false
    ) {
        if (sessionHandle == 0L) return
        nativeSetInput(sessionHandle, throttle, brake, steerAngle, handbrake, horn)
    }

    /**
     * Ends the current simulation session and returns the final score.
     */
    fun endSession(): Int {
        if (sessionHandle == 0L) return 0
        val score = nativeEndSession(sessionHandle)
        sessionHandle = 0L
        return score
    }

    /**
     * Injects a terrain chunk into the C++ engine.
     */
    fun injectChunk(chunkX: Int, chunkY: Int, data: ByteArray): Boolean {
        if (sessionHandle == 0L) return false
        return nativeInjectChunk(sessionHandle, chunkX, chunkY, data)
    }

    /**
     * Evicts a terrain chunk from the C++ engine.
     */
    fun evictChunk(chunkX: Int, chunkY: Int): Boolean {
        if (sessionHandle == 0L) return false
        return nativeEvictChunk(sessionHandle, chunkX, chunkY)
    }

    /**
     * Gets friction at a world position from the C++ engine.
     */
    fun getFriction(worldX: Float, worldZ: Float, wet: Boolean): Float {
        if (sessionHandle == 0L) return 0.8f
        return nativeGetFriction(sessionHandle, worldX, worldZ, wet)
    }

    /**
     * Initializes the renderer with a surface.
     */
    fun initRenderer(
        surface: Any,
        width: Int,
        height: Int,
        assetManager: android.content.res.AssetManager
    ) {
        nativeInitRenderer(surface, width, height, assetManager)
    }

    /**
     * Renders a frame.
     */
    fun renderFrame(): Boolean {
        if (sessionHandle == 0L) return false
        return nativeRenderFrame(sessionHandle)
    }

    /**
     * Sets the camera position and look-at target.
     */
    fun setCamera(eyeX: Float, eyeY: Float, eyeZ: Float, targetX: Float, targetY: Float, targetZ: Float) {
        nativeSetCamera(eyeX, eyeY, eyeZ, targetX, targetY, targetZ)
    }

    /**
     * Resizes the renderer viewport.
     */
    fun resizeRenderer(width: Int, height: Int) {
        nativeResizeRenderer(width, height)
    }

    /**
     * Checks if a session is currently active.
     */
    fun isSessionActive(): Boolean = sessionHandle != 0L
}