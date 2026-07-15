#pragma once

class AckermannSolver {
public:
    AckermannSolver();
    ~AckermannSolver();

    void solve(float steerAngle, float wheelbase, float trackWidth,
               float& leftAngle, float& rightAngle);
};
