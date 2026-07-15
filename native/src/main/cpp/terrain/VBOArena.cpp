#include "VBOArena.h"

VBOArena::VBOArena(size_t size) : m_capacity(size), m_used(0), m_vbo(0) {}
VBOArena::~VBOArena() = default;

size_t VBOArena::allocateChunk() { return INVALID_OFFSET; }
void VBOArena::freeChunk(size_t offset) {}

size_t VBOArena::getUsed() const { return m_used; }
size_t VBOArena::getCapacity() const { return m_capacity; }
uint32_t VBOArena::getBuffer() const { return m_vbo; }
