#pragma once

#include <cstdint>
#include <memory>
#include <jni.h>
#include <android/native_window.h>
#include <Eigen/Dense>
#include "../physics/PhysicsEngine.h"

class ShaderManager;
class TextureManager;
class GLTFLoader;

class Renderer {
public:
    Renderer();
    ~Renderer();

    // Initialization
    bool init(ANativeWindow* window, int width, int height, jobject assetManager);
    void resize(int width, int height);

    // Rendering
    bool renderFrame(const VehicleState& state);
    void setCamera(const Eigen::Vector3f& eye, const Eigen::Vector3f& target);

    // Cleanup
    void shutdown();

private:
    struct Impl;
    std::unique_ptr<Impl> pImpl;

    // Platform-specific rendering
    bool initGL(ANativeWindow* window, int width, int height);
    bool initVulkan(ANativeWindow* window, int width, int height);

    // Rendering pipeline
    void renderScene();
    void renderVehicle(const VehicleState& state);
    void renderTerrain();
    void renderUI();

    // Shaders
    std::unique_ptr<ShaderManager> m_shaderManager;
    std::unique_ptr<TextureManager> m_textureManager;
    std::unique_ptr<GLTFLoader> m_gltfLoader;

    // State
    bool m_isInitialized = false;
    bool m_useVulkan = false;
    int m_width = 0;
    int m_height = 0;
    Eigen::Matrix4f m_viewMatrix;
    Eigen::Matrix4f m_projMatrix;

    // Timing
    float m_frameTimeMs = 0;
};