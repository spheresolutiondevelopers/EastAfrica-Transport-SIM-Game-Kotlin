package com.transportsim.data.config

import com.transportsim.domain.models.TerrainChunk
import com.transportsim.domain.repositories.TerrainRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerrainPreloader @Inject constructor(
    private val terrainRepository: TerrainRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isActive = false

    data class VehicleState(
        val x: Float,
        val z: Float,
        val vx: Float,
        val vz: Float
    )

    fun start(vehicleStateFlow: Flow<VehicleState>, preloadRadius: Int = 2, lookaheadSeconds: Float = 2f) {
        if (isActive) return
        isActive = true

        scope.launch {
            vehicleStateFlow
                .sample(100.milliseconds) // 10 Hz
                .collect { state ->
                    if (!isActive) return@collect

                    val futureX = state.x + state.vx * lookaheadSeconds
                    val futureZ = state.z + state.vz * lookaheadSeconds

                    val centerX = (futureX / 128).toInt()
                    val centerZ = (futureZ / 128).toInt()

                    // Preload a square ring around the predicted position
                    for (dx in -preloadRadius..preloadRadius) {
                        for (dz in -preloadRadius..preloadRadius) {
                            val cx = centerX + dx
                            val cz = centerZ + dz
                            scope.launch {
                                terrainRepository.loadChunk(cx, cz)
                            }
                        }
                    }

                    // Eviction: we could evict chunks far away, but for simplicity,
                    // the repository's LRU eviction will handle it.
                }
        }
    }

    fun stop() {
        isActive = false
        scope.cancel()
    }
}