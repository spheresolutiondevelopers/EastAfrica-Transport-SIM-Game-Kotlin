#include "TerrainChunk.h"
#include "../jni/native_glue.h"
#include <algorithm>
#include <cstring>
#include <cmath>

TerrainChunk::TerrainChunk(int chunkX, int chunkY)
    : m_chunkX(chunkX)
    , m_chunkY(chunkY)
    , m_elevationGrid(GRID_SIZE, 0.0f)
    , m_surfaceClassGrid(GRID_SIZE, 0) {
    m_lastUsed = std::chrono::steady_clock::now().time_since_epoch().count();
}

TerrainChunk::~TerrainChunk() = default;

bool TerrainChunk::loadFromMemory(const void* data, size_t length, size_t vboOffset) {
    if (!data || length < CHUNK_DATA_SIZE + CLASS_DATA_SIZE) {
        LOGE("Invalid chunk data size: %zu (expected %zu)",
             length, CHUNK_DATA_SIZE + CLASS_DATA_SIZE);
        return false;
    }

    const uint8_t* ptr = static_cast<const uint8_t*>(data);

    // Read elevation grid (65x65 floats)
    const float* elevData = reinterpret_cast<const float*>(ptr);
    std::copy(elevData, elevData + GRID_SIZE, m_elevationGrid.begin());
    ptr += CHUNK_DATA_SIZE;

    // Read surface class grid (65x65 uint8_t)
    const uint8_t* classData = ptr;
    std::copy(classData, classData + GRID_SIZE, m_surfaceClassGrid.begin());
    ptr += CLASS_DATA_SIZE;

    // Read asset metadata (if available)
    size_t remaining = length - (CHUNK_DATA_SIZE + CLASS_DATA_SIZE);
    if (remaining > 0) {
        // Parse asset entries (simplified format)
        // In a real implementation, this would deserialize proper structured data
        uint32_t assetCount = 0;
        if (remaining >= sizeof(uint32_t)) {
            std::memcpy(&assetCount, ptr, sizeof(uint32_t));
            ptr += sizeof(uint32_t);
            remaining -= sizeof(uint32_t);
        }

        for (uint32_t i = 0; i < assetCount && remaining > 0; ++i) {
            AssetEntry entry;
            // Simplified: just read positions
            // In a real implementation, this would parse full asset data
            if (remaining >= sizeof(float) * 3) {
                std::memcpy(&entry.posX, ptr, sizeof(float));
                ptr += sizeof(float);
                std::memcpy(&entry.posY, ptr, sizeof(float));
                ptr += sizeof(float);
                std::memcpy(&entry.posZ, ptr, sizeof(float));
                ptr += sizeof(float);
                remaining -= sizeof(float) * 3;
                entry.typeId = "unknown";
                entry.rotY = 0;
                entry.scale = 1;
                m_assets.push_back(entry);
            }
        }
    }

    // Store VBO offset
    m_vboOffset = vboOffset;

    // Generate vertex data and upload to VBO
    // In a real implementation, we'd compute vertex positions, normals, UVs
    // and upload to the shared VBO at the given offset

    LOGI("Loaded chunk (%d, %d): %zu elevation points, %zu assets",
         m_chunkX, m_chunkY, m_elevationGrid.size(), m_assets.size());

    return true;
}

float TerrainChunk::getElevationAt(float localX, float localZ) const {
    // Clamp to [0, 1] range
    float x = std::clamp(localX, 0.0f, 1.0f);
    float z = std::clamp(localZ, 0.0f, 1.0f);

    // Convert to grid coordinates
    float gridX = x * (RESOLUTION - 1);
    float gridZ = z * (RESOLUTION - 1);

    return bilinearInterpolate(m_elevationGrid, gridX, gridZ);
}

float TerrainChunk::getFrictionAt(float localX, float localZ, bool wet) const {
    uint8_t surfaceClass = getSurfaceClassAt(localX, localZ);

    // Friction lookup table (simplified)
    // In a real implementation, this would use a proper friction table
    // with bilinear interpolation for smooth transitions
    if (wet) {
        switch (surfaceClass) {
            case 0: return 0.55f; // Tarmac
            case 1: return 0.48f; // Aged tarmac
            case 2: return 0.25f; // Dirt
            case 3: return 0.18f; // Wet dirt
            default: return 0.48f;
        }
    } else {
        switch (surfaceClass) {
            case 0: return 0.85f; // Tarmac
            case 1: return 0.80f; // Aged tarmac
            case 2: return 0.55f; // Dirt
            case 3: return 0.28f; // Wet dirt
            default: return 0.80f;
        }
    }
}

uint8_t TerrainChunk::getSurfaceClassAt(float localX, float localZ) const {
    float x = std::clamp(localX, 0.0f, 1.0f);
    float z = std::clamp(localZ, 0.0f, 1.0f);

    float gridX = x * (RESOLUTION - 1);
    float gridZ = z * (RESOLUTION - 1);

    // Nearest neighbor for class (or bilinear for smooth transitions)
    int ix = static_cast<int>(std::round(gridX));
    int iz = static_cast<int>(std::round(gridZ));
    ix = std::clamp(ix, 0, RESOLUTION - 1);
    iz = std::clamp(iz, 0, RESOLUTION - 1);

    return m_surfaceClassGrid[iz * RESOLUTION + ix];
}

float TerrainChunk::bilinearInterpolate(const std::vector<float>& grid, float x, float y) const {
    int x0 = static_cast<int>(std::floor(x));
    int x1 = std::min(x0 + 1, RESOLUTION - 1);
    int y0 = static_cast<int>(std::floor(y));
    int y1 = std::min(y0 + 1, RESOLUTION - 1);

    float fx = x - x0;
    float fy = y - y0;

    float v00 = grid[y0 * RESOLUTION + x0];
    float v10 = grid[y0 * RESOLUTION + x1];
    float v01 = grid[y1 * RESOLUTION + x0];
    float v11 = grid[y1 * RESOLUTION + x1];

    float v0 = v00 + (v10 - v00) * fx;
    float v1 = v01 + (v11 - v01) * fx;

    return v0 + (v1 - v0) * fy;
}