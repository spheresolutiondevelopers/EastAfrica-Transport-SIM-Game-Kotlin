package com.transportsim.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.transportsim.data.database.entities.TerrainChunkMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TerrainChunkMetadataDao {
    @Query("SELECT * FROM terrain_chunk_metadata WHERE chunkKey = :chunkKey")
    suspend fun getByKey(chunkKey: String): TerrainChunkMetadataEntity?

    @Query("SELECT * FROM terrain_chunk_metadata WHERE isOnDisk = 1")
    suspend fun getAvailableChunks(): List<TerrainChunkMetadataEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(metadata: TerrainChunkMetadataEntity)

    @Query("UPDATE terrain_chunk_metadata SET isOnDisk = 0, lastAccessedAt = :evictedAt WHERE chunkKey = :chunkKey")
    suspend fun markEvicted(chunkKey: String, evictedAt: String)

    @Query("DELETE FROM terrain_chunk_metadata WHERE isOnDisk = 0 AND lastAccessedAt < :olderThan")
    suspend fun deleteEvictedOlderThan(olderThan: String)

    @Query("SELECT COUNT(*) FROM terrain_chunk_metadata WHERE isOnDisk = 1")
    suspend fun countAvailable(): Int
}