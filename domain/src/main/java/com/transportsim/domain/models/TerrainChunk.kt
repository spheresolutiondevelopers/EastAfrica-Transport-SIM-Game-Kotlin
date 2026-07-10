package com.transportsim.domain.models

data class TerrainChunk(
    val chunkX: Int,
    val chunkY: Int,
    val sizeM: Float = 128f,
    val resolution: Int = 65  // 65x65 elevation grid
) {
    val elevationGridSize: Int = resolution * resolution
}