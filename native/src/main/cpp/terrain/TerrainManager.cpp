#include "TerrainManager.h"
#include "TerrainChunk.h"
#include "VBOArena.h"
#include "../jni/native_glue.h"

#include <algorithm>
#include <cmath>
#include <chrono>
#include <cstring>

TerrainManager::TerrainManager()
    : m_vboArena(std::make_unique<VBOArena>(TerrainConfig::VBO_ARENA_SIZE)) {
    LOGI("TerrainManager initialized with VBO arena size: %zu MB",
         TerrainConfig::VBO_ARENA_SIZE / (1024 * 1024));
}

TerrainManager::~TerrainManager() = default;

bool TerrainManager::injectChunk(int chunkX, int chunkY, const void* data, size_t length) {
    std::unique_lock lock(m_mutex);

    // Check if already loaded
    ChunkKey key{chunkX, chunkY};
    if (m_activeChunks.find(key) != m_activeChunks.end()) {
        LOGW("Chunk (%d, %d) already loaded", chunkX, chunkY);
        return false;
    }

    // Evict if at capacity
    if (m_activeChunks.size() >= TerrainConfig::MAX_ACTIVE_CHUNKS) {
        evictLRUChunk();
    }

    // Allocate VBO space
    size_t vboOffset = m_vboArena->allocateChunk();
    if (vboOffset == VBOArena::INVALID_OFFSET) {
        LOGE("Failed to allocate VBO space for chunk (%d, %d)", chunkX, chunkY);
        // Try to evict and retry
        evictLRUChunk();
        vboOffset = m_vboArena->allocateChunk();
        if (vboOffset == VBOArena::INVALID_OFFSET) {
            LOGE("VBO allocation failed after eviction");
            return false;
        }
    }

    // Create and load chunk
    auto chunk = std::make_unique<TerrainChunk>(chunkX, chunkY);
    if (!chunk->loadFromMemory(data, length, vboOffset)) {
        LOGE("Failed to load chunk data for (%d, %d)", chunkX, chunkY);
        m_vboArena->freeChunk(vboOffset);
        return false;
    }

    // Store chunk
    chunk->markUsed();
    m_activeChunks[key] = std::move(chunk);

    LOGI("Chunk (%d, %d) loaded. Active: %zu/%d, VBO used: %zu/%zu",
         chunkX, chunkY,
         m_activeChunks.size(),
         TerrainConfig::MAX_ACTIVE_CHUNKS,
         m_vboArena->getUsed(),
         m_vboArena->getCapacity());

    return true;
}

bool TerrainManager::evictChunk(int chunkX, int chunkY) {
    std::unique_lock lock(m_mutex);

    ChunkKey key{chunkX, chunkY};
    auto it = m_activeChunks.find(key);
    if (it == m_activeChunks.end()) {
        return false;
    }

    // Free VBO space
    if (it->second->hasVBO()) {
        m_vboArena->freeChunk(it->second->getVBOOffset());
    }

    m_activeChunks.erase(it);
    LOGI("Chunk (%d, %d) evicted", chunkX, chunkY);
    return true;
}

bool TerrainManager::hasChunk(int chunkX, int chunkY) const {
    std::shared_lock lock(m_mutex);
    ChunkKey key{chunkX, chunkY};
    return m_activeChunks.find(key) != m_activeChunks.end();
}

float TerrainManager::getFriction(float worldX, float worldZ, bool wet) const {
    std::shared_lock lock(m_mutex);

    auto key = getChunkKey(worldX, worldZ);
    auto it = m_activeChunks.find(key);
    if (it == m_activeChunks.end()) {
        // Default friction for unloaded chunks
        return wet ? 0.48f : 0.80f;
    }

    float localX = getLocalCoord(worldX, key.x);
    float localZ = getLocalCoord(worldZ, key.y);

    return it->second->getFrictionAt(localX, localZ, wet);
}

float TerrainManager::getElevation(float worldX, float worldZ) const {
    std::shared_lock lock(m_mutex);

    auto key = getChunkKey(worldX, worldZ);
    auto it = m_activeChunks.find(key);
    if (it == m_activeChunks.end()) {
        return 0.0f;
    }

    float localX = getLocalCoord(worldX, key.x);
    float localZ = getLocalCoord(worldZ, key.y);

    return it->second->getElevationAt(localX, localZ);
}

uint32_t TerrainManager::getVBO() const {
    return m_vboArena->getBuffer();
}

size_t TerrainManager::getVBOUsed() const {
    return m_vboArena->getUsed();
}

size_t TerrainManager::getVBOCapacity() const {
    return m_vboArena->getCapacity();
}

const TerrainChunk* TerrainManager::getActiveChunk(int chunkX, int chunkY) const {
    std::shared_lock lock(m_mutex);
    ChunkKey key{chunkX, chunkY};
    auto it = m_activeChunks.find(key);
    if (it == m_activeChunks.end()) {
        return nullptr;
    }
    return it->second.get();
}

TerrainManager::ChunkKey TerrainManager::getChunkKey(float worldX, float worldZ) const {
    int cx = static_cast<int>(std::floor(worldX / TerrainConfig::CHUNK_SIZE));
    int cy = static_cast<int>(std::floor(worldZ / TerrainConfig::CHUNK_SIZE));
    return {cx, cy};
}

float TerrainManager::getLocalCoord(float worldCoord, int chunkCoord) const {
    float local = worldCoord - chunkCoord * TerrainConfig::CHUNK_SIZE;
    return local / TerrainConfig::CHUNK_SIZE; // 0..1 range
}

void TerrainManager::evictLRUChunk() {
    if (m_activeChunks.empty()) return;

    // Find the least recently used chunk
    auto lruIt = m_activeChunks.begin();
    auto minTime = lruIt->second->getLastUsedTime();

    for (auto it = m_activeChunks.begin(); it != m_activeChunks.end(); ++it) {
        if (it->second->getLastUsedTime() < minTime) {
            minTime = it->second->getLastUsedTime();
            lruIt = it;
        }
    }

    // Evict it
    LOGI("Evicting LRU chunk (%d, %d) (last used: %lld ms ago)",
         lruIt->first.x, lruIt->first.y,
         std::chrono::duration_cast<std::chrono::milliseconds>(
             std::chrono::steady_clock::now().time_since_epoch()).count() - minTime);

    if (lruIt->second->hasVBO()) {
        m_vboArena->freeChunk(lruIt->second->getVBOOffset());
    }
    m_activeChunks.erase(lruIt);
}