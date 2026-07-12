#include "PhysicsEngine.h"
#include "Pacejka.h"
#include "Ackermann.h"
#include "Suspension.h"
#include "../jni/native_glue.h"

#include <chrono>
#include <nlohmann/json.hpp>

using json = nlohmann::json;

PhysicsEngine::PhysicsEngine()
    : m_pacejka(std::make_unique<PacejkaModel>())
    , m_ackermann(std::make_unique<AckermannSolver>())
    , m_suspension(std::make_unique<SuspensionModel>()) {
}

PhysicsEngine::~PhysicsEngine() = default;

void PhysicsEngine::initSession(const std::string& configJson,
                                 const std::string& segmentsJson,
                                 const std::string& stopsJson) {
    // Parse JSON configuration
    auto config = json::parse(configJson);
    
    // Extract vehicle specs
    m_spec.vehicleId = config["vehicleId"].get<int>();
    m_spec.typeId = config["typeId"].get<std::string>();
    m_spec.massKg = config["massKg"].get<float>();
    m_spec.maxPayloadKg = config["maxPayloadKg"].get<float>();
    m_spec.wheelbaseM = config["wheelbaseM"].get<float>();
    m_spec.trackWidthM = config["trackWidthM"].get<float>();
    m_spec.comHeightLadenM = config["comHeightLadenM"].get<float>();
    m_spec.momentInertiaIzz = config["momentInertiaIzz"].get<float>();
    m_spec.maxSpeedKph = config["maxSpeedKph"].get<float>();
    m_spec.peakAccelMs2 = config["peakAccelMs2"].get<float>();
    m_spec.emergencyBrakeMs2 = config["emergencyBrakeMs2"].get<float>();
    m_spec.minTurnRadiusM = config["minTurnRadiusM"].get<float>();
    m_spec.aeroDragCd = config["dragCd"].get<float>();
    m_spec.frontalAreaM2 = config["frontalAreaM2"].get<float>();
    m_spec.enginePowerKw = config["enginePowerKw"].get<float>();
    m_spec.peakTorqueNm = config["peakTorqueNm"].get<float>();
    m_spec.fuelCapacityL = config["fuelCapacityL"].get<float>();
    m_spec.fuelConsumptionBase = config["fuelConsumptionBase"].get<float>();
    m_spec.pacejkaB = config["pacejkaB"].get<float>();
    m_spec.pacejkaC = config["pacejkaC"].get<float>();
    m_spec.pacejkaD = config["pacejkaD"].get<float>();
    m_spec.pacejkaE = config["pacejkaE"].get<float>();
    m_spec.frontSpringRate = config["frontSpringRate"].get<float>();
    m_spec.rearSpringRate = config["rearSpringRate"].get<float>();
    m_spec.frontDamping = config["frontDamping"].get<float>();
    m_spec.rearDamping = config["rearDamping"].get<float>();
    m_spec.currentFuelL = config["currentFuelL"].get<float>();
    m_spec.engineHealthPct = config["engineHealthPct"].get<float>();
    m_spec.tyreConditionPct = config["tyreConditionPct"].get<float>();
    m_spec.passengerCapacity = config["passengerCapacity"].get<int>();
    m_spec.fuelEffMult = config["fuelEffMult"].get<float>();
    m_spec.brakeMs2 = config["brakeMs2"].get<float>();
    m_spec.navAccuracyPct = config["navAccuracyPct"].get<float>();

    // Parse road segments
    auto segments = json::parse(segmentsJson);
    m_segments.clear();
    for (const auto& seg : segments) {
        RoadSegment rs;
        rs.segmentId = seg["segmentId"].get<int>();
        rs.distAlongRouteM = seg["distAlongRouteM"].get<float>();
        rs.segmentLengthM = seg["segmentLengthM"].get<float>();
        rs.frictionMuDry = seg["frictionMuDry"].get<float>();
        rs.frictionMuWet = seg["frictionMuWet"].get<float>();
        rs.iriValue = seg["iriValue"].get<float>();
        rs.iriClass = seg["iriClass"].get<int>();
        rs.superelevationDeg = seg["superelevationDeg"].get<float>();
        rs.speedLimitKph = seg["speedLimitKph"].get<int>();
        rs.lanes = seg["lanes"].get<int>();
        rs.roadWidthM = seg["roadWidthM"].get<float>();
        rs.hasCrosswind = seg["hasCrosswind"].get<bool>();
        rs.hasPothole = seg["hasPothole"].get<bool>();
        rs.isIntersection = seg["isIntersection"].get<bool>();
        rs.surfaceTypeId = seg["surfaceTypeId"].get<std::string>();
        m_segments.push_back(rs);
    }

    // Parse bus stops
    auto stops = json::parse(stopsJson);
    m_stops.clear();
    for (const auto& stop : stops) {
        BusStop bs;
        bs.stopId = stop["stopId"].get<int>();
        bs.stopOrder = stop["stopOrder"].get<int>();
        bs.stopName = stop["stopName"].get<std::string>();
        bs.worldX = stop["worldX"].get<float>();
        bs.worldZ = stop["worldZ"].get<float>();
        bs.distFromOriginKm = stop["distFromOriginKm"].get<float>();
        bs.dwellTimeS = stop["dwellTimeS"].get<int>();
        bs.onTimeToleranceM = stop["onTimeToleranceM"].get<float>();
        bs.maxPassengers = stop["maxPassengers"].get<int>();
        bs.passengerDemandPeak = stop["passengerDemandPeak"].get<float>();
        bs.stopType = stop["stopType"].get<std::string>();
        bs.hasShelter = stop["hasShelter"].get<bool>();
        bs.isTerminal = stop["isTerminal"].get<bool>();
        m_stops.push_back(bs);
    }

    // Initialize state
    m_state.pos = Eigen::Vector3f(0, 0, 0);
    m_state.orientation = Eigen::Quaternionf::Identity();
    m_state.velocity = Eigen::Vector3f::Zero();
    m_state.angularVelocity = Eigen::Vector3f::Zero();
    m_state.speedKph = 0;
    m_state.gear = 1;
    m_state.rpm = 600;
    m_state.fuelL = m_spec.currentFuelL;
    m_state.engineTempC = 65;
    m_state.odometerM = 0;
    m_state.throttleInput = 0;
    m_state.brakeInput = 0;
    m_state.steerAngleDeg = 0;
    m_state.score = 0;
    m_state.suspensionDeflections.fill(0);
    m_state.wheelSpeeds.fill(0);

    m_isActive = true;
    m_totalTime = 0;
    m_stepCount = 0;
    m_scoreAccumulator = 0;
    m_fuelSinceLastSave = 0;
    m_autosaveTimer = 0;

    // Reset input
    m_input.throttle = 0;
    m_input.brake = 0;
    m_input.steerAngle = 0;
    m_input.handbrake = false;
    m_input.horn = false;

    LOGI("Physics session initialized for vehicle %d", m_spec.vehicleId);
}

bool PhysicsEngine::step(float dt) {
    if (!m_isActive) return false;

    auto startTime = std::chrono::high_resolution_clock::now();

    // Apply input to state
    m_state.throttleInput = m_input.throttle;
    m_state.brakeInput = m_input.brake;
    m_state.steerAngleDeg = m_input.steerAngle;

    // RK4 integration
    integrateRK4(dt);

    // Update subsystems
    updateGear();
    updateFuel(dt);
    updateOdometer(dt);
    checkCollisions();
    checkBusStops();
    checkSpeedLimits();
    updateScore(dt);

    m_totalTime += dt;
    m_stepCount++;

    // Autosave every 5 seconds
    m_autosaveTimer += dt;
    if (m_autosaveTimer >= 5.0f) {
        m_autosaveTimer = 0;
        // Trigger autosave callback (would call back to Kotlin)
        // In a real implementation, we'd notify Kotlin via JNI callback
    }

    // Performance metrics
    auto endTime = std::chrono::high_resolution_clock::now();
    auto duration = std::chrono::duration_cast<std::chrono::microseconds>(endTime - startTime);
    m_physicsTimeUs = duration.count();

    return m_isActive;
}

void PhysicsEngine::setInput(const VehicleInput& input) {
    m_input = input;
}

VehicleState PhysicsEngine::getState() const {
    return m_state;
}

std::string PhysicsEngine::getStateJson() const {
    json j;
    j["posX"] = m_state.pos.x();
    j["posY"] = m_state.pos.y();
    j["posZ"] = m_state.pos.z();
    j["headingDeg"] = m_state.orientation.toRotationMatrix().eulerAngles(0, 1, 2).z() * 180.0f / M_PI;
    j["speedKph"] = m_state.speedKph;
    j["gear"] = m_state.gear;
    j["rpm"] = m_state.rpm;
    j["fuelL"] = m_state.fuelL;
    j["engineTempC"] = m_state.engineTempC;
    j["suspensionDeflectionFL"] = m_state.suspensionDeflections[0];
    j["suspensionDeflectionFR"] = m_state.suspensionDeflections[1];
    j["suspensionDeflectionRL"] = m_state.suspensionDeflections[2];
    j["suspensionDeflectionRR"] = m_state.suspensionDeflections[3];
    j["score"] = m_state.score;
    j["odometerKm"] = m_state.odometerM / 1000.0f;
    j["throttleInput"] = m_state.throttleInput;
    j["brakeInput"] = m_state.brakeInput;
    j["steerAngleDeg"] = m_state.steerAngleDeg;
    return j.dump();
}

std::string PhysicsEngine::getTrafficLightsJson() const {
    // In a real implementation, this would return traffic light states
    // For now, return empty array
    json j = json::array();
    return j.dump();
}

std::string PhysicsEngine::getMetricsJson() const {
    json j;
    j["frameTimeUs"] = static_cast<long long>(m_frameTimeUs);
    j["physicsStepUs"] = static_cast<long long>(m_physicsTimeUs);
    j["renderTimeUs"] = 0;
    j["objectCount"] = m_segments.size() + m_stops.size();
    j["drawCalls"] = 0;
    j["triangles"] = 0;
    j["fps"] = m_stepCount > 0 ? m_stepCount / m_totalTime : 0;
    return j.dump();
}

int PhysicsEngine::getScore() const {
    return m_state.score;
}

int PhysicsEngine::endSession() {
    m_isActive = false;
    LOGI("Session ended for vehicle %d, final score: %d", m_spec.vehicleId, m_state.score);
    return m_state.score;
}

void PhysicsEngine::integrateRK4(float dt) {
    // RK4 integration for rigid-body dynamics
    // Simplified: in a real implementation, this would handle full 6-DOF physics
    // including Pacejka forces, suspension, aerodynamics, etc.
    
    float speedMs = m_state.speedKph / 3.6f;
    
    // Simple acceleration model
    float dragForce = 0.5f * 1.225f * m_spec.aeroDragCd * m_spec.frontalAreaM2 * speedMs * speedMs;
    float engineForce = (m_input.throttle * m_spec.peakAccelMs2 * m_spec.massKg);
    float brakeForce = (m_input.brake * m_spec.emergencyBrakeMs2 * m_spec.massKg);
    
    float accelMs2 = (engineForce - dragForce - brakeForce) / m_spec.massKg;
    accelMs2 = std::clamp(accelMs2, -m_spec.emergencyBrakeMs2, m_spec.peakAccelMs2);
    
    // Update speed
    speedMs += accelMs2 * dt;
    speedMs = std::max(0.0f, speedMs);
    speedMs = std::min(speedMs, m_spec.maxSpeedKph / 3.6f);
    
    m_state.speedKph = speedMs * 3.6f;
    
    // Update position (simplified: along forward direction)
    float forwardVec = speedMs * dt;
    m_state.pos.z() += forwardVec; // Assuming Z is forward
    
    // Update RPM
    float gearRatio = 1.0f + (m_state.gear - 1) * 0.15f;
    m_state.rpm = speedMs * 60.0f * gearRatio / (0.3f * M_PI);
    m_state.rpm = std::clamp(m_state.rpm, 600.0f, 4500.0f);
}

void PhysicsEngine::updateGear() {
    // Simple gear shifting logic
    float speed = m_state.speedKph;
    if (speed < 10) m_state.gear = 1;
    else if (speed < 25) m_state.gear = 2;
    else if (speed < 42) m_state.gear = 3;
    else if (speed < 58) m_state.gear = 4;
    else if (speed < 75) m_state.gear = 5;
    else m_state.gear = 6;
}

void PhysicsEngine::updateFuel(float dt) {
    float speedKmh = m_state.speedKph;
    float consumptionPerKm = m_spec.fuelConsumptionBase / 100.0f;
    float distanceKm = speedKmh * dt / 3600.0f;
    float fuelUsed = consumptionPerKm * distanceKm * (1.0f / m_spec.fuelEffMult);
    m_state.fuelL -= fuelUsed;
    m_state.fuelL = std::max(0.0f, m_state.fuelL);
    m_fuelSinceLastSave += fuelUsed;

    // Low fuel warning
    float fuelPct = (m_state.fuelL / m_spec.fuelCapacityL) * 100.0f;
    if (fuelPct < 15.0f) {
        callbackOnLowFuel(fuelPct);
    }
}

void PhysicsEngine::updateOdometer(float dt) {
    float distanceM = (m_state.speedKph / 3.6f) * dt;
    m_state.odometerM += distanceM;
}

void PhysicsEngine::checkCollisions() {
    // Simplified collision detection
    // In a real implementation, this would check against world objects
    // and traffic vehicles
}

void PhysicsEngine::checkBusStops() {
    // Check if vehicle is at a bus stop
    // In a real implementation, this would check proximity to bus stops
    // and trigger boarding events
}

void PhysicsEngine::checkSpeedLimits() {
    // Check if vehicle is speeding and trigger overspeed warning
    for (const auto& segment : m_segments) {
        float distM = m_state.odometerM;
        if (distM >= segment.distAlongRouteM &&
            distM < segment.distAlongRouteM + segment.segmentLengthM) {
            if (m_state.speedKph > segment.speedLimitKph) {
                callbackOnOverspeed(m_state.speedKph, segment.speedLimitKph);
            }
            break;
        }
    }
}

void PhysicsEngine::updateScore(float dt) {
    // Score accumulates based on speed, smoothness, and other factors
    m_state.score += static_cast<int>(m_state.speedKph * 0.1f * dt);
}