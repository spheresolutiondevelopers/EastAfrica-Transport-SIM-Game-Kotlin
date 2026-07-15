#pragma once

class SuspensionModel {
public:
    SuspensionModel();
    ~SuspensionModel();

    float calculateForce(float displacement, float velocity, float springRate, float damping);
};
