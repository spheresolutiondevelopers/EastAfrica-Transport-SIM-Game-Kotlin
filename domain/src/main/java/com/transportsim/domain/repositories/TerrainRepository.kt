package com.transportsim.domain.repositories

import com.transportsim.domain.models.SurfaceType
import com.transportsim.domain.models.TerrainChunk
import kotlinx.coroutines.flow.Flow

interface TerrainRepository {
    suspend fun loadChunk(chunkX: Int, chunkY: Int): TerrainChunk
    suspend fun evictChunk(chunkX: Int, chunkY: Int)
    suspend fun getSurfaceTypeAt(worldX: Float, worldZ: Float): SurfaceType
    suspend fun getFrictionAt(worldX: Float, worldZ: Float, wet: Boolean): Float
    fun observeLoadedChunks(): Flow<Set<TerrainChunk>>
}