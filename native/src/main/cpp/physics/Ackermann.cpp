#include "Ackermann.h"

AckermannSolver::AckermannSolver() = default;
AckermannSolver::~AckermannSolver() = default;

void AckermannSolver::solve(float steerAngle, float wheelbase, float trackWidth,
                            float& leftAngle, float& rightAngle) {
    leftAngle = steerAngle;
    rightAngle = steerAngle;
}
