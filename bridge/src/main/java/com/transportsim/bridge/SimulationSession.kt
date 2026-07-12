package com.transportsim.bridge

import com.transportsim.bridge.models.NativeInputState
import com.transportsim.bridge.models.NativeRoadSegment
import com.transportsim.bridge.models.NativeBusStop
import com.transportsim.domain.models.ResolvedVehicleConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages a running simulation session.
 * Handles the physics loop, input processing, and lifecycle.
 */
@Singleton
class SimulationSession @Inject constructor(
    private val nativeEngine: NativeEngine,
    private val nativeCallbacks: NativeCallbacks
) {
    private var physicsJob: Job? = null
    private var renderJob: Job? = null
    private var stateUpdateJob: Job? = null
    private var sessionHandle: Long = 0L
    private var isRunning = false
    private var isPaused = false
    private var rendererInitialized = false

    // Input state
    private var currentInput = NativeInputState(
        throttle = 0f,
        brake = 0f,
        steerAngle = 0f,
        handbrake = false,
        horn = false,
        timestampMs = 0L
    )

    /**
     * Starts a new simulation session.
     */
    suspend fun startSession(
        vehicleConfig: ResolvedVehicleConfig,
        roadSegments: List<NativeRoadSegment>,
        busStops: List<NativeBusStop>
    ): Long {
        if (isRunning) {
            throw IllegalStateException("Session already running")
        }

        sessionHandle = nativeEngine.initSession(vehicleConfig, roadSegments, busStops)
        isRunning = true
        isPaused = false

        // Start the physics loop
        startPhysicsLoop()

        // Start the render loop (if renderer is initialized)
        if (rendererInitialized) {
            startRenderLoop()
        }

        // Start the state update loop (60 Hz)
        startStateUpdateLoop()

        return sessionHandle
    }

    /**
     * Pauses the simulation.
     */
    fun pause() {
        isPaused = true
    }

    /**
     * Resumes the simulation.
     */
    fun resume() {
        isPaused = false
    }

    /**
     * Stops the current simulation session.
     * Returns the final score.
     */
    suspend fun stopSession(): Int = withContext(Dispatchers.IO) {
        isRunning = false
        physicsJob?.cancel()
        renderJob?.cancel()
        stateUpdateJob?.cancel()

        val score = nativeEngine.endSession()
        sessionHandle = 0L
        score
    }

    /**
     * Updates the vehicle input state.
     */
    fun setInput(
        throttle: Float = 0f,
        brake: Float = 0f,
        steerAngle: Float = 0f,
        handbrake: Boolean = false,
        horn: Boolean = false
    ) {
        currentInput = currentInput.copy(
            throttle = throttle.coerceIn(0f, 1f),
            brake = brake.coerceIn(0f, 1f),
            steerAngle = steerAngle.coerceIn(-35f, 35f),
            handbrake = handbrake,
            horn = horn,
            timestampMs = System.currentTimeMillis()
        )

        if (isRunning && !isPaused) {
            nativeEngine.setInput(currentInput)
        }
    }

    /**
     * Gets the current vehicle state.
     */
    fun getVehicleState() = nativeEngine.vehicleState.value

    /**
     * Gets the current simulation metrics.
     */
    fun getMetrics() = nativeEngine.metrics.value

    /**
     * Gets the current traffic light states.
     */
    fun getTrafficLights() = nativeEngine.getTrafficLights()

    /**
     * Checks if the session is running.
     */
    fun isRunning() = isRunning

    /**
     * Checks if the session is paused.
     */
    fun isPaused() = isPaused

    /**
     * Initializes the renderer.
     */
    fun initRenderer(
        surface: Any,
        width: Int,
        height: Int,
        assetManager: android.content.res.AssetManager
    ) {
        nativeEngine.initRenderer(surface, width, height, assetManager)
        rendererInitialized = true
        if (isRunning) {
            startRenderLoop()
        }
    }

    /**
     * Resizes the renderer viewport.
     */
    fun resizeRenderer(width: Int, height: Int) {
        nativeEngine.resizeRenderer(width, height)
    }

    /**
     * Sets the camera position.
     */
    fun setCamera(eyeX: Float, eyeY: Float, eyeZ: Float, targetX: Float, targetY: Float, targetZ: Float) {
        nativeEngine.setCamera(eyeX, eyeY, eyeZ, targetX, targetY, targetZ)
    }

    // ─── Private loops ──────────────────────────────────────────

    private fun startPhysicsLoop() {
        physicsJob = CoroutineScope(Dispatchers.Default).launch {
            var previousTime = System.nanoTime()
            while (isRunning && isActive) {
                if (!isPaused) {
                    val currentTime = System.nanoTime()
                    val dt = (currentTime - previousTime) / 1_000_000_000f
                    val stepDt = 0.001f // 1ms fixed step

                    // Step the physics at 1000 Hz
                    var remaining = dt
                    while (remaining > 0 && isActive && isRunning) {
                        val step = stepDt.coerceAtMost(remaining)
                        nativeEngine.stepPhysics(step)
                        remaining -= step
                        // Use a small delay to avoid busylooping
                        if (remaining > 0) {
                            delay(1)
                        }
                    }
                    previousTime = currentTime
                } else {
                    delay(16) // Wait while paused
                }
            }
        }
    }

    private fun startStateUpdateLoop() {
        stateUpdateJob = CoroutineScope(Dispatchers.Default).launch {
            while (isRunning && isActive) {
                if (!isPaused) {
                    nativeEngine.updateVehicleState()
                    // Update metrics
                    val metrics = nativeEngine.getMetrics()
                    // Metrics are already exposed via flow
                }
                delay(16) // 60 Hz
            }
        }
    }

    private fun startRenderLoop() {
        renderJob = CoroutineScope(Dispatchers.Default).launch {
            while (isRunning && isActive) {
                if (!isPaused) {
                    nativeEngine.renderFrame()
                }
                delay(16) // 60 Hz
            }
        }
    }

    // ─── Terrain streaming ─────────────────────────────────────

    fun injectChunk(chunkX: Int, chunkY: Int, data: ByteArray): Boolean {
        return nativeEngine.injectChunk(chunkX, chunkY, data)
    }

    fun evictChunk(chunkX: Int, chunkY: Int): Boolean {
        return nativeEngine.evictChunk(chunkX, chunkY)
    }

    fun getFriction(worldX: Float, worldZ: Float, wet: Boolean): Float {
        return nativeEngine.getFriction(worldX, worldZ, wet)
    }
}