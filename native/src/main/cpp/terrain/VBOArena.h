#pragma once
#include <cstdint>
#include <cstddef>
#include <vector>

class VBOArena {
public:
    static constexpr size_t INVALID_OFFSET = static_cast<size_t>(-1);

    explicit VBOArena(size_t size);
    ~VBOArena();

    size_t allocateChunk();
    void freeChunk(size_t offset);

    size_t getUsed() const;
    size_t getCapacity() const;
    uint32_t getBuffer() const;

private:
    size_t m_capacity;
    size_t m_used;
    uint32_t m_vbo;
};
