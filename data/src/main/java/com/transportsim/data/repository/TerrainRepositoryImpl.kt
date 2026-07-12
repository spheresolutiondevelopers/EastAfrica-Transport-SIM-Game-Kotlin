package com.transportsim.data.repository

import com.transportsim.data.datasource.TerrainChunkDataSource
import com.transportsim.domain.models.SurfaceType
import com.transportsim.domain.models.TerrainChunk
import com.transportsim.domain.repositories.TerrainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerrainRepositoryImpl @Inject constructor(
    private val dataSource: TerrainChunkDataSource
) : TerrainRepository {

    // In a real implementation, we'd have a native bridge to inject chunks into C++.
    // For now, we simulate loading.
    private val loadedChunks = MutableStateFlow<Set<TerrainChunk>>(emptySet())

    override suspend fun loadChunk(chunkX: Int, chunkY: Int): TerrainChunk {
        val chunk = TerrainChunk(chunkX, chunkY)
        val data = dataSource.readChunk(chunkX, chunkY)
        if (data == null) {
            // Chunk not found – maybe generate a default or fallback
            // For now, just create an empty chunk
            loadedChunks.update { it + chunk }
            return chunk
        }
        // In a real implementation, we'd call native: nativeEngine.injectChunk(chunkX, chunkY, data)
        loadedChunks.update { it + chunk }
        return chunk
    }

    override suspend fun evictChunk(chunkX: Int, chunkY: Int) {
        loadedChunks.update { it.filter { chunk -> chunk.chunkX != chunkX || chunk.chunkY != chunkY }.toSet() }
        // In a real implementation, we'd call native: nativeEngine.evictChunk(chunkX, chunkY)
    }

    override suspend fun getSurfaceTypeAt(worldX: Float, worldZ: Float): SurfaceType {
        // In a real implementation, this would query the native terrain engine
        // For now, return a default surface type
        return SurfaceType.TARMAC_AGED
    }

    override suspend fun getFrictionAt(worldX: Float, worldZ: Float, wet: Boolean): Float {
        // Query native engine or the loaded chunks
        return if (wet) 0.48f else 0.80f
    }

    override fun observeLoadedChunks(): Flow<Set<TerrainChunk>> {
        return loadedChunks.asStateFlow()
    }
}