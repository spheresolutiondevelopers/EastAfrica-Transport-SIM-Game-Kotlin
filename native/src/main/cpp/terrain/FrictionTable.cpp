#include "FrictionTable.h"

float FrictionTable::getFriction(int surfaceType, bool wet) {
    return wet ? 0.4f : 0.8f;
}
