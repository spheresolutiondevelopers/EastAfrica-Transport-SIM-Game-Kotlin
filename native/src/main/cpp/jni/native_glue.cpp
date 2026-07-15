#include "native_glue.h"
#include "../physics/PhysicsEngine.h"
#include "../terrain/TerrainManager.h"
#include "../renderer/Renderer.h"
#include <cstring>

static NativeGlobals g_globals;

NativeGlobals::NativeGlobals() = default;
NativeGlobals::~NativeGlobals() = default;

NativeGlobals& getGlobals() {
    return g_globals;
}

jlong getNativeHandle(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID fid = env->GetFieldID(cls, "nativeHandle", "J");
    return env->GetLongField(thiz, fid);
}

void setNativeHandle(JNIEnv* env, jobject thiz, jlong handle) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID fid = env->GetFieldID(cls, "nativeHandle", "J");
    env->SetLongField(thiz, fid, handle);
}

// Callback implementations
void callbackOnCollision(int severity, int objectId, float damage) {
    JNIEnv* env = nullptr;
    if (g_globals.javaVM->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) {
        LOGE("Failed to get JNIEnv for callback");
        return;
    }
    if (g_globals.callbackObject && g_globals.onCollisionMethod) {
        env->CallVoidMethod(g_globals.callbackObject, g_globals.onCollisionMethod,
                            severity, objectId, damage);
    }
}

void callbackOnStopReached(int stopId, const std::string& stopName, int passengers) {
    JNIEnv* env = nullptr;
    if (g_globals.javaVM->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) return;
    if (g_globals.callbackObject && g_globals.onStopReachedMethod) {
        jstring jName = env->NewStringUTF(stopName.c_str());
        env->CallVoidMethod(g_globals.callbackObject, g_globals.onStopReachedMethod,
                            stopId, jName, passengers);
        env->DeleteLocalRef(jName);
    }
}

void callbackOnScoreUpdate(int score) {
    JNIEnv* env = nullptr;
    if (g_globals.javaVM->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) return;
    if (g_globals.callbackObject && g_globals.onScoreUpdateMethod) {
        env->CallVoidMethod(g_globals.callbackObject, g_globals.onScoreUpdateMethod, score);
    }
}

void callbackOnRouteProgress(float progress) {
    JNIEnv* env = nullptr;
    if (g_globals.javaVM->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) return;
    if (g_globals.callbackObject && g_globals.onRouteProgressMethod) {
        env->CallVoidMethod(g_globals.callbackObject, g_globals.onRouteProgressMethod, progress);
    }
}

void callbackOnVehicleStateUpdate(const std::string& stateJson) {
    JNIEnv* env = nullptr;
    if (g_globals.javaVM->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) return;
    if (g_globals.callbackObject && g_globals.onVehicleStateMethod) {
        jstring jState = env->NewStringUTF(stateJson.c_str());
        env->CallVoidMethod(g_globals.callbackObject, g_globals.onVehicleStateMethod, jState);
        env->DeleteLocalRef(jState);
    }
}

void callbackOnLowFuel(float fuelPercent) {
    JNIEnv* env = nullptr;
    if (g_globals.javaVM->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) return;
    if (g_globals.callbackObject && g_globals.onLowFuelMethod) {
        env->CallVoidMethod(g_globals.callbackObject, g_globals.onLowFuelMethod, fuelPercent);
    }
}

void callbackOnOverspeed(float currentSpeed, float limit) {
    JNIEnv* env = nullptr;
    if (g_globals.javaVM->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) return;
    if (g_globals.callbackObject && g_globals.onOverspeedMethod) {
        env->CallVoidMethod(g_globals.callbackObject, g_globals.onOverspeedMethod,
                            currentSpeed, limit);
    }
}