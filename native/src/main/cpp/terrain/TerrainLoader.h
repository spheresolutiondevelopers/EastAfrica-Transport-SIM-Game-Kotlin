#pragma once
#include <string>

class TerrainLoader {
public:
    TerrainLoader();
    ~TerrainLoader();

    bool load(const std::string& path);
};
