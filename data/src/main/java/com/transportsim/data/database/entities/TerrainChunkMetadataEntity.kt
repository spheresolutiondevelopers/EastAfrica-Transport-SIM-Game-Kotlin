package com.transportsim.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "terrain_chunk_metadata")
data class TerrainChunkMetadataEntity(
    @PrimaryKey
    val chunkKey: String, // "cx_cy"
    val chunkX: Int,
    val chunkY: Int,
    val lastAccessedAt: String,
    val isOnDisk: Int = 1 // 0 = false, 1 = true
)