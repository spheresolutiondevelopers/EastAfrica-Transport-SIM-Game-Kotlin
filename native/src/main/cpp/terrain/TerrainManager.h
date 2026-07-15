#pragma once

#include <unordered_map>
#include <memory>
#include <string>
#include <vector>
#include <mutex>
#include <shared_mutex>
#include <cstdint>

class TerrainChunk;
class VBOArena;

struct TerrainConfig {
    static constexpr float CHUNK_SIZE = 128.0f;
    static constexpr int ELEVATION_RES = 65;
    static constexpr int MAX_ACTIVE_CHUNKS = 15;
    static constexpr size_t VBO_ARENA_SIZE = 2 * 1024 * 1024; // 2 MB
};

class TerrainManager {
public:
    TerrainManager();
    ~TerrainManager();

    // Chunk management
    bool injectChunk(int chunkX, int chunkY, const void* data, size_t length);
    bool evictChunk(int chunkX, int chunkY);
    bool hasChunk(int chunkX, int chunkY) const;

    // Friction query
    float getFriction(float worldX, float worldZ, bool wet) const;

    // Elevation query
    float getElevation(float worldX, float worldZ) const;

    // VBO access
    uint32_t getVBO() const;
    size_t getVBOUsed() const;
    size_t getVBOCapacity() const;

    // Getters
    const TerrainChunk* getActiveChunk(int chunkX, int chunkY) const;

private:
    struct ChunkKey {
        int x;
        int y;
        bool operator==(const ChunkKey& other) const {
            return x == other.x && y == other.y;
        }
    };

    struct ChunkKeyHash {
        size_t operator()(const ChunkKey& k) const {
            return static_cast<size_t>(k.x) * 31 + static_cast<size_t>(k.y);
        }
    };

    // Active chunks map
    std::unordered_map<ChunkKey, std::unique_ptr<TerrainChunk>, ChunkKeyHash> m_activeChunks;
    
    // Mutex for thread safety
    mutable std::shared_mutex m_mutex;

    // VBO arena
    std::unique_ptr<VBOArena> m_vboArena;

    // Helper functions
    ChunkKey getChunkKey(float worldX, float worldZ) const;
    float getLocalCoord(float worldCoord, int chunkCoord) const;
    void evictLRUChunk();
};