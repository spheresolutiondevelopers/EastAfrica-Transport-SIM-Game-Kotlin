#include "Interpolation.h"

float Interpolation::lerp(float a, float b, float t) {
    return a + (b - a) * t;
}
