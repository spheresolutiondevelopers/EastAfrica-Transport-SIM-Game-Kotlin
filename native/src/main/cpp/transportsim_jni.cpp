#include <jni.h>
#include <string>
#include <memory>
#include <vector>
#include <android/native_window_jni.h>
#include <nlohmann/json.hpp>

#include "../physics/PhysicsEngine.h"
#include "../terrain/TerrainManager.h"
#include "../renderer/Renderer.h"
#include "native_glue.h"

using json = nlohmann::json;

// Global engine instances (managed by the JNI layer)
static std::unique_ptr<PhysicsEngine> g_physicsEngine;
static std::unique_ptr<TerrainManager> g_terrainManager;
static std::unique_ptr<Renderer> g_renderer;

// ─── JNI Helpers ──────────────────────────────────────────────

jlong getSessionHandle(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID fid = env->GetFieldID(cls, "sessionHandle", "J");
    return env->GetLongField(thiz, fid);
}

void setSessionHandle(JNIEnv* env, jobject thiz, jlong handle) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID fid = env->GetFieldID(cls, "sessionHandle", "J");
    env->SetLongField(thiz, fid, handle);
}

// ─── Native Methods ───────────────────────────────────────────

extern "C" {

// ─── Session Management ──────────────────────────────────────

JNIEXPORT jlong JNICALL
Java_com_transportsim_bridge_NativeEngine_nativeInitSession(
    JNIEnv* env,
    jobject thiz,
    jstring configJson,
    jstring segmentsJson,
    jstring stopsJson) {

    const char* configStr = env->GetStringUTFChars(configJson, nullptr);
    const char* segmentsStr = env->GetStringUTFChars(segmentsJson, nullptr);
    const char* stopsStr = env->GetStringUTFChars(stopsJson, nullptr);

    // Create or reset engine
    g_physicsEngine = std::make_unique<PhysicsEngine>();
    g_physicsEngine->initSession(configStr, segmentsStr, stopsStr);

    // Store session handle (just a pointer cast to jlong)
    jlong handle = reinterpret_cast<jlong>(g_physicsEngine.get());
    setSessionHandle(env, thiz, handle);

    env->ReleaseStringUTFChars(configJson, configStr);
    env->ReleaseStringUTFChars(segmentsJson, segmentsStr);
    env->ReleaseStringUTFChars(stopsJson, stopsStr);

    return handle;
}

JNIEXPORT jboolean JNICALL
Java_com_transportsim_bridge_NativeEngine_nativeStepPhysics(
    JNIEnv* env,
    jobject thiz,
    jlong sessionHandle,
    jfloat dt) {

    PhysicsEngine* engine = reinterpret_cast<PhysicsEngine*>(sessionHandle);
    if (!engine) return JNI_FALSE;

    bool active = engine->step(dt);
    return active ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jstring JNICALL
Java_com_transportsim_bridge_NativeEngine_nativeGetVehicleState(
    JNIEnv* env,
    jobject thiz,
    jlong sessionHandle) {

    PhysicsEngine* engine = reinterpret_cast<PhysicsEngine*>(sessionHandle);
    if (!engine) {
        return env->NewStringUTF("{}");
    }

    std::string jsonState = engine->getStateJson();
    return env->NewStringUTF(jsonState.c_str());
}

JNIEXPORT jstring JNICALL
Java_com_transportsim_bridge_NativeEngine_nativeGetTrafficLights(
    JNIEnv* env,
    jobject thiz,
    jlong sessionHandle) {

    PhysicsEngine* engine = reinterpret_cast<PhysicsEngine*>(sessionHandle);
    if (!engine) {
        return env->NewStringUTF("[]");
    }

    std::string jsonLights = engine->getTrafficLightsJson();
    return env->NewStringUTF(jsonLights.c_str());
}

JNIEXPORT jstring JNICALL
Java_com_transportsim_bridge_NativeEngine_nativeGetMetrics(
    JNIEnv* env,
    jobject thiz,
    jlong sessionHandle) {

    PhysicsEngine* engine = reinterpret_cast<PhysicsEngine*>(sessionHandle);
    if (!engine) {
        return env->NewStringUTF("{}");
    }

    std::string jsonMetrics = engine->getMetricsJson();
    return env->NewStringUTF(jsonMetrics.c_str());
}

JNIEXPORT void JNICALL
Java_com_transportsim_bridge_NativeEngine_nativeSetInput(
    JNIEnv* env,
    jobject thiz,
    jlong sessionHandle,
    jfloat throttle,
    jfloat brake,
    jfloat steerAngle,
    jboolean handbrake,
    jboolean horn) {

    PhysicsEngine* engine = reinterpret_cast<PhysicsEngine*>(sessionHandle);
    if (!engine) return;

    VehicleInput input;
    input.throttle = throttle;
    input.brake = brake;
    input.steerAngle = steerAngle;
    input.handbrake = handbrake == JNI_TRUE;
    input.horn = horn == JNI_TRUE;

    engine->setInput(input);
}

JNIEXPORT jint JNICALL
Java_com_transportsim_bridge_NativeEngine_nativeEndSession(
    JNIEnv* env,
    jobject thiz,
    jlong sessionHandle) {

    PhysicsEngine* engine = reinterpret_cast<PhysicsEngine*>(sessionHandle);
    if (!engine) return 0;

    int score = engine->endSession();
    g_physicsEngine.reset();
    setSessionHandle(env, thiz, 0);

    return score;
}

// ─── Terrain Methods ──────────────────────────────────────────

JNIEXPORT jboolean JNICALL
Java_com_transportsim_bridge_NativeEngine_nativeInjectChunk(
    JNIEnv* env,
    jobject thiz,
    jlong sessionHandle,
    jint chunkX,
    jint chunkY,
    jbyteArray data) {

    if (!g_terrainManager) {
        g_terrainManager = std::make_unique<TerrainManager>();
    }

    jsize len = env->GetArrayLength(data);
    jbyte* bytes = env->GetByteArrayElements(data, nullptr);

    bool success = g_terrainManager->injectChunk(chunkX, chunkY, bytes, len);

    env->ReleaseByteArrayElements(data, bytes, JNI_ABORT);
    return success ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_com_transportsim_bridge_NativeEngine_nativeEvictChunk(
    JNIEnv* env,
    jobject thiz,
    jlong sessionHandle,
    jint chunkX,
    jint chunkY) {

    if (!g_terrainManager) return JNI_FALSE;

    bool success = g_terrainManager->evictChunk(chunkX, chunkY);
    return success ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jfloat JNICALL
Java_com_transportsim_bridge_NativeEngine_nativeGetFriction(
    JNIEnv* env,
    jobject thiz,
    jlong sessionHandle,
    jfloat worldX,
    jfloat worldZ,
    jboolean wet) {

    if (!g_terrainManager) return 0.8f;

    return g_terrainManager->getFriction(worldX, worldZ, wet == JNI_TRUE);
}

// ─── Renderer Methods ─────────────────────────────────────────

JNIEXPORT void JNICALL
Java_com_transportsim_bridge_NativeEngine_nativeInitRenderer(
    JNIEnv* env,
    jobject thiz,
    jobject surface,
    jint width,
    jint height,
    jobject assetManager) {

    if (!g_renderer) {
        g_renderer = std::make_unique<Renderer>();
    }

    // Get the native window from the surface
    ANativeWindow* window = ANativeWindow_fromSurface(env, surface);
    if (!window) {
        LOGE("Failed to get native window from surface");
        return;
    }

    g_renderer->init(window, width, height, assetManager);
}

JNIEXPORT jboolean JNICALL
Java_com_transportsim_bridge_NativeEngine_nativeRenderFrame(
    JNIEnv* env,
    jobject thiz,
    jlong sessionHandle) {

    if (!g_renderer) return JNI_FALSE;

    PhysicsEngine* engine = reinterpret_cast<PhysicsEngine*>(sessionHandle);
    if (!engine) return JNI_FALSE;

    VehicleState state = engine->getState();
    return g_renderer->renderFrame(state) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_com_transportsim_bridge_NativeEngine_nativeSetCamera(
    JNIEnv* env,
    jobject thiz,
    jfloat eyeX, jfloat eyeY, jfloat eyeZ,
    jfloat targetX, jfloat targetY, jfloat targetZ) {

    if (!g_renderer) return;
    g_renderer->setCamera(
        Eigen::Vector3f(eyeX, eyeY, eyeZ),
        Eigen::Vector3f(targetX, targetY, targetZ)
    );
}

JNIEXPORT void JNICALL
Java_com_transportsim_bridge_NativeEngine_nativeResizeRenderer(
    JNIEnv* env,
    jobject thiz,
    jint width,
    jint height) {

    if (!g_renderer) return;
    g_renderer->resize(width, height);
}

} // extern "C"