package com.transportsim.domain.usecases

import com.transportsim.domain.models.TerrainChunk
import com.transportsim.domain.repositories.TerrainRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class TerrainPreloadUseCase(
    private val terrainRepository: TerrainRepository,
    private val preloadRadius: Int = 2, // chunks in each direction
    private val lookaheadSeconds: Float = 2.0f
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    data class VehicleState(
        val x: Float,
        val z: Float,
        val vx: Float,
        val vz: Float
    )

    fun startStreaming(vehicleStateFlow: Flow<VehicleState>) {
        scope.launch {
            vehicleStateFlow
                .sample(100.milliseconds) // sample at 10 Hz
                .collect { state ->
                    // Predict future position
                    val futureX = state.x + state.vx * lookaheadSeconds
                    val futureZ = state.z + state.vz * lookaheadSeconds

                    val centerChunkX = (futureX / 128).toInt()
                    val centerChunkY = (futureZ / 128).toInt()

                    // Preload ring around predicted position
                    val chunksToLoad = mutableListOf<TerrainChunk>()
                    for (dx in -preloadRadius..preloadRadius) {
                        for (dz in -preloadRadius..preloadRadius) {
                            val cx = centerChunkX + dx
                            val cy = centerChunkY + dz
                            chunksToLoad.add(TerrainChunk(cx, cy))
                        }
                    }

                    // Ask repository to load these chunks (async)
                    chunksToLoad.forEach { chunk ->
                        scope.launch {
                            terrainRepository.loadChunk(chunk.chunkX, chunk.chunkY)
                        }
                    }

                    // Evict chunks far away (simplified: just keep loaded set; repository handles eviction)
                }
        }
    }

    fun stopStreaming() {
        scope.cancel()
    }
}