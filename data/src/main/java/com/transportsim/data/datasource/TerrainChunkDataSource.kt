package com.transportsim.data.datasource

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerrainChunkDataSource @Inject constructor(
    private val context: Context
) {
    suspend fun readChunk(chunkX: Int, chunkY: Int): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = "terrain/chunk_${chunkX}_${chunkY}.bin"
                context.assets.open(fileName).use { inputStream ->
                    inputStream.readBytes()
                }
            } catch (e: Exception) {
                // Chunk might not exist locally – try internal storage
                try {
                    val file = File(context.filesDir, "terrain/chunk_${chunkX}_${chunkY}.bin")
                    if (file.exists()) {
                        file.readBytes()
                    } else {
                        null
                    }
                } catch (e2: Exception) {
                    null
                }
            }
        }
    }

    suspend fun chunkExists(chunkX: Int, chunkY: Int): Boolean {
        return withContext(Dispatchers.IO) {
            val assetPath = "terrain/chunk_${chunkX}_${chunkY}.bin"
            try {
                context.assets.open(assetPath).close()
                true
            } catch (e: Exception) {
                val file = File(context.filesDir, assetPath)
                file.exists()
            }
        }
    }

    suspend fun writeChunkToDisk(chunkX: Int, chunkY: Int, data: ByteArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val dir = File(context.filesDir, "terrain")
                if (!dir.exists()) dir.mkdirs()
                val file = File(dir, "chunk_${chunkX}_${chunkY}.bin")
                file.writeBytes(data)
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}