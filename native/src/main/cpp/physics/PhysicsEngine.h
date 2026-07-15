#pragma once

#include <vector>
#include <array>
#include <string>
#include <memory>
#include <cmath>
#include <Eigen/Dense>

// Forward declarations
class PacejkaModel;
class AckermannSolver;
class SuspensionModel;

// Vehicle specification struct
struct VehicleSpec {
    int vehicleId;
    std::string typeId;
    float massKg;
    float maxPayloadKg;
    float wheelbaseM;
    float trackWidthM;
    float comHeightLadenM;
    float momentInertiaIzz;
    float maxSpeedKph;
    float peakAccelMs2;
    float emergencyBrakeMs2;
    float minTurnRadiusM;
    float aeroDragCd;
    float frontalAreaM2;
    float enginePowerKw;
    float peakTorqueNm;
    float fuelCapacityL;
    float fuelConsumptionBase;
    float pacejkaB;
    float pacejkaC;
    float pacejkaD;
    float pacejkaE;
    float frontSpringRate;
    float rearSpringRate;
    float frontDamping;
    float rearDamping;
    float currentFuelL;
    float engineHealthPct;
    float tyreConditionPct;
    int passengerCapacity;
    float fuelEffMult;
    float brakeMs2;
    float navAccuracyPct;
};

// Road segment struct
struct RoadSegment {
    int segmentId;
    float distAlongRouteM;
    float segmentLengthM;
    float frictionMuDry;
    float frictionMuWet;
    float iriValue;
    int iriClass;
    float superelevationDeg;
    int speedLimitKph;
    int lanes;
    float roadWidthM;
    bool hasCrosswind;
    bool hasPothole;
    bool isIntersection;
    std::string surfaceTypeId;
};

// Bus stop struct
struct BusStop {
    int stopId;
    int stopOrder;
    std::string stopName;
    float worldX;
    float worldZ;
    float distFromOriginKm;
    int dwellTimeS;
    float onTimeToleranceM;
    int maxPassengers;
    float passengerDemandPeak;
    std::string stopType;
    bool hasShelter;
    bool isTerminal;
};

// Traffic light state
struct TrafficLightState {
    int index;
    int phase;  // 0=green, 1=amber, 2=red
    int timerMs;
};

// Vehicle state structure
struct VehicleState {
    Eigen::Vector3f pos;
    Eigen::Quaternionf orientation;
    Eigen::Vector3f velocity;
    Eigen::Vector3f angularVelocity;
    float speedKph;
    int gear;
    float rpm;
    float fuelL;
    float engineTempC;
    float odometerM;
    float throttleInput;
    float brakeInput;
    float steerAngleDeg;
    int score;
    std::array<float, 4> suspensionDeflections;
    std::array<float, 4> wheelSpeeds;
};

// Input state
struct VehicleInput {
    float throttle;     // 0..1
    float brake;        // 0..1
    float steerAngle;   // -35..35 degrees
    bool handbrake;
    bool horn;
};

class PhysicsEngine {
public:
    PhysicsEngine();
    ~PhysicsEngine();

    // Initialization
    void initSession(const std::string& configJson,
                     const std::string& segmentsJson,
                     const std::string& stopsJson);
    
    // Physics step
    bool step(float dt);
    
    // Input
    void setInput(const VehicleInput& input);
    
    // State queries
    VehicleState getState() const;
    std::string getStateJson() const;
    std::string getTrafficLightsJson() const;
    std::string getMetricsJson() const;
    int getScore() const;
    
    // Termination
    int endSession();

private:
    // Physics subsystems
    std::unique_ptr<PacejkaModel> m_pacejka;
    std::unique_ptr<AckermannSolver> m_ackermann;
    std::unique_ptr<SuspensionModel> m_suspension;
    
    // State
    VehicleState m_state;
    VehicleInput m_input;
    VehicleSpec m_spec;
    
    // Road data
    std::vector<RoadSegment> m_segments;
    std::vector<BusStop> m_stops;
    
    // Metrics
    float m_totalTime = 0.0f;
    int m_stepCount = 0;
    float m_frameTimeUs = 0.0f;
    float m_physicsTimeUs = 0.0f;
    
    // Session state
    bool m_isActive = false;
    float m_scoreAccumulator = 0.0f;
    float m_fuelSinceLastSave = 0.0f;
    float m_autosaveTimer = 0.0f;
    
    // Integration helpers
    struct Derivative {
        Eigen::Vector3f vel;
        Eigen::Vector3f accel;
        Eigen::Vector3f angVel;
        Eigen::Vector3f angAccel;
    };
    
    Derivative evaluate(const VehicleState& state, float t, float dt);
    void integrateRK4(float dt);
    void updateGear();
    void updateFuel(float dt);
    void updateOdometer(float dt);
    void checkCollisions();
    void checkBusStops();
    void checkSpeedLimits();
    void updateScore(float dt);
};
