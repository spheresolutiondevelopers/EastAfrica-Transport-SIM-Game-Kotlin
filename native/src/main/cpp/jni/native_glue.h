#pragma once

#include <jni.h>
#include <string>
#include <memory>

// Forward declarations
class PhysicsEngine;
class TerrainManager;
class Renderer;

// Global state for native callbacks
struct NativeGlobals {
    std::unique_ptr<PhysicsEngine> physicsEngine;
    std::unique_ptr<TerrainManager> terrainManager;
    std::unique_ptr<Renderer> renderer;
    JavaVM* javaVM = nullptr;
    jobject callbackObject = nullptr;
    jmethodID onCollisionMethod = nullptr;
    jmethodID onStopReachedMethod = nullptr;
    jmethodID onScoreUpdateMethod = nullptr;
    jmethodID onRouteProgressMethod = nullptr;
    jmethodID onVehicleStateMethod = nullptr;
    jmethodID onLowFuelMethod = nullptr;
    jmethodID onOverspeedMethod = nullptr;
};

// Global singleton access
NativeGlobals& getGlobals();

// JNI helper functions
jlong getNativeHandle(JNIEnv* env, jobject thiz);
void setNativeHandle(JNIEnv* env, jobject thiz, jlong handle);

// Callback helpers
void callbackOnCollision(int severity, int objectId, float damage);
void callbackOnStopReached(int stopId, const std::string& stopName, int passengers);
void callbackOnScoreUpdate(int score);
void callbackOnRouteProgress(float progress);
void callbackOnVehicleStateUpdate(const std::string& stateJson);
void callbackOnLowFuel(float fuelPercent);
void callbackOnOverspeed(float currentSpeed, float limit);