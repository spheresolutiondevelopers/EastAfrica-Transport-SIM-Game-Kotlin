#include "Suspension.h"

SuspensionModel::SuspensionModel() = default;
SuspensionModel::~SuspensionModel() = default;

float SuspensionModel::calculateForce(float displacement, float velocity, float springRate, float damping) {
    return springRate * displacement + damping * velocity;
}
