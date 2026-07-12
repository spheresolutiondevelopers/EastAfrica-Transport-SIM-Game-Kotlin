package com.transportsim.bridge.models

import com.transportsim.bridge.annotations.Keep

/**
 * Wraps a native pointer to terrain chunk data.
 * The native code owns the memory; this class just holds the pointer.
 */
@Keep
data class TerrainChunkData(
    val chunkX: Int,
    val chunkY: Int,
    val nativePtr: Long  // Pointer to C++ TerrainChunk object
) {
    fun isValid(): Boolean = nativePtr != 0L
}