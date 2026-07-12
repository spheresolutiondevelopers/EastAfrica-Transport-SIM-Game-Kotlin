#pragma once

#include <cstdint>
#include <vector>
#include <array>
#include <chrono>

class TerrainChunk {
public:
    // Constants
    static constexpr int RESOLUTION = 65;      // 65x65 grid
    static constexpr int GRID_SIZE = RESOLUTION * RESOLUTION;
    static constexpr size_t CHUNK_DATA_SIZE = GRID_SIZE * 4;   // elevation (float)
    static constexpr size_t CLASS_DATA_SIZE = GRID_SIZE * 1;   // class (uint8_t)
    static constexpr size_t ASSET_BLOCK_SIZE = 1024 * 4;       // 4KB for asset metadata

    TerrainChunk(int chunkX, int chunkY);
    ~TerrainChunk();

    // Load binary data from memory
    bool loadFromMemory(const void* data, size_t length, size_t vboOffset);

    // Queries
    float getElevationAt(float localX, float localZ) const;
    float getFrictionAt(float localX, float localZ, bool wet) const;
    uint8_t getSurfaceClassAt(float localX, float localZ) const;

    // VBO
    bool hasVBO() const { return m_vboOffset != SIZE_MAX; }
    size_t getVBOOffset() const { return m_vboOffset; }

    // LRU tracking
    void markUsed() { m_lastUsed = std::chrono::steady_clock::now().time_since_epoch().count(); }
    int64_t getLastUsedTime() const { return m_lastUsed; }

    // Getters
    int getChunkX() const { return m_chunkX; }
    int getChunkY() const { return m_chunkY; }

private:
    int m_chunkX;
    int m_chunkY;
    size_t m_vboOffset = SIZE_MAX;

    // Grid data
    std::vector<float> m_elevationGrid;      // 65x65 floats
    std::vector<uint8_t> m_surfaceClassGrid; // 65x65 uint8_t

    // Asset metadata (parsed from binary)
    struct AssetEntry {
        std::string typeId;
        float posX;
        float posY;
        float posZ;
        float rotY;
        float scale;
    };
    std::vector<AssetEntry> m_assets;

    // LRU tracking
    int64_t m_lastUsed = 0;

    // Helpers
    float bilinearInterpolate(const std::vector<float>& grid, float x, float y) const;
    float bilinearInterpolateClass(const std::vector<uint8_t>& grid, float x, float y) const;
    void generateVertexData(float* vertexBuffer, size_t bufferSize) const;
};