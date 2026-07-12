#include "Renderer.h"
#include "ShaderManager.h"
#include "TextureManager.h"
#include "GLTFLoader.h"
#include "../jni/native_glue.h"
#include <GLES3/gl3.h>
#include <EGL/egl.h>
#include <cstring>

// Simple GLES3 renderer implementation
struct Renderer::Impl {
    EGLDisplay display = EGL_NO_DISPLAY;
    EGLSurface surface = EGL_NO_SURFACE;
    EGLContext context = EGL_NO_CONTEXT;
    ANativeWindow* window = nullptr;
};

Renderer::Renderer()
    : pImpl(std::make_unique<Impl>())
    , m_shaderManager(std::make_unique<ShaderManager>())
    , m_textureManager(std::make_unique<TextureManager>())
    , m_gltfLoader(std::make_unique<GLTFLoader>()) {
}

Renderer::~Renderer() {
    shutdown();
}

bool Renderer::init(ANativeWindow* window, int width, int height, jobject assetManager) {
    if (!window || width <= 0 || height <= 0) {
        LOGE("Invalid window or dimensions");
        return false;
    }

    m_width = width;
    m_height = height;
    pImpl->window = window;

    // Try Vulkan first, fallback to GLES3
    if (!initVulkan(window, width, height)) {
        LOGI("Vulkan init failed, falling back to GLES3");
        if (!initGL(window, width, height)) {
            LOGE("GLES3 init failed");
            return false;
        }
        m_useVulkan = false;
    } else {
        m_useVulkan = true;
    }

    // Initialize shaders
    if (!m_shaderManager->init(assetManager, m_useVulkan)) {
        LOGE("Shader initialization failed");
        return false;
    }

    // Initialize texture manager
    if (!m_textureManager->init(assetManager)) {
        LOGE("Texture manager initialization failed");
        return false;
    }

    // Initialize GLTF loader
    if (!m_gltfLoader->init(assetManager)) {
        LOGE("GLTF loader initialization failed");
        return false;
    }

    m_isInitialized = true;
    LOGI("Renderer initialized (Vulkan: %s)", m_useVulkan ? "true" : "false");
    return true;
}

void Renderer::resize(int width, int height) {
    if (!m_isInitialized) return;
    m_width = width;
    m_height = height;
    // Update projection matrix
    float aspect = static_cast<float>(width) / height;
    m_projMatrix = Eigen::Matrix4f::Identity();
    // Simplified perspective projection
    // In a real implementation, this would be a proper projection matrix
}

bool Renderer::renderFrame(const VehicleState& state) {
    if (!m_isInitialized) return false;

    // Clear buffers
    if (m_useVulkan) {
        // Vulkan rendering path
    } else {
        glClearColor(0.1f, 0.15f, 0.3f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    // Render scene
    renderScene();

    // Render vehicle
    renderVehicle(state);

    // Render terrain
    renderTerrain();

    // Render UI overlay
    renderUI();

    // Swap buffers
    if (m_useVulkan) {
        // Vulkan swapchain present
    } else {
        eglSwapBuffers(pImpl->display, pImpl->surface);
    }

    return true;
}

void Renderer::setCamera(const Eigen::Vector3f& eye, const Eigen::Vector3f& target) {
    // Calculate view matrix
    Eigen::Vector3f forward = (target - eye).normalized();
    Eigen::Vector3f right = forward.cross(Eigen::Vector3f::UnitY()).normalized();
    Eigen::Vector3f up = right.cross(forward);

    m_viewMatrix = Eigen::Matrix4f::Identity();
    m_viewMatrix.block<3, 1>(0, 0) = right;
    m_viewMatrix.block<3, 1>(0, 1) = up;
    m_viewMatrix.block<3, 1>(0, 2) = -forward;
    m_viewMatrix.block<3, 1>(0, 3) = -right.dot(eye), -up.dot(eye), forward.dot(eye);
}

void Renderer::shutdown() {
    if (pImpl->display != EGL_NO_DISPLAY) {
        eglMakeCurrent(pImpl->display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
        if (pImpl->surface != EGL_NO_SURFACE) {
            eglDestroySurface(pImpl->display, pImpl->surface);
        }
        if (pImpl->context != EGL_NO_CONTEXT) {
            eglDestroyContext(pImpl->display, pImpl->context);
        }
        eglTerminate(pImpl->display);
    }
    m_isInitialized = false;
}

bool Renderer::initGL(ANativeWindow* window, int width, int height) {
    // Initialize EGL
    pImpl->display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (pImpl->display == EGL_NO_DISPLAY) {
        LOGE("EGL display initialization failed");
        return false;
    }

    EGLint major, minor;
    if (!eglInitialize(pImpl->display, &major, &minor)) {
        LOGE("EGL initialization failed");
        return false;
    }

    // Choose EGL config
    const EGLint configAttribs[] = {
        EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
        EGL_RENDERABLE_TYPE, EGL_OPENGL_ES3_BIT,
        EGL_BLUE_SIZE, 8,
        EGL_GREEN_SIZE, 8,
        EGL_RED_SIZE, 8,
        EGL_ALPHA_SIZE, 8,
        EGL_DEPTH_SIZE, 24,
        EGL_STENCIL_SIZE, 8,
        EGL_NONE
    };

    EGLConfig config;
    EGLint numConfigs;
    if (!eglChooseConfig(pImpl->display, configAttribs, &config, 1, &numConfigs) || numConfigs == 0) {
        LOGE("EGL config selection failed");
        return false;
    }

    // Create context
    const EGLint contextAttribs[] = {
        EGL_CONTEXT_CLIENT_VERSION, 3,
        EGL_NONE
    };
    pImpl->context = eglCreateContext(pImpl->display, config, EGL_NO_CONTEXT, contextAttribs);
    if (pImpl->context == EGL_NO_CONTEXT) {
        LOGE("EGL context creation failed");
        return false;
    }

    // Create surface
    pImpl->surface = eglCreateWindowSurface(pImpl->display, config, window, nullptr);
    if (pImpl->surface == EGL_NO_SURFACE) {
        LOGE("EGL surface creation failed");
        return false;
    }

    // Make context current
    if (!eglMakeCurrent(pImpl->display, pImpl->surface, pImpl->surface, pImpl->context)) {
        LOGE("EGL make current failed");
        return false;
    }

    LOGI("GLES3 initialized (version %s)", glGetString(GL_VERSION));
    return true;
}

bool Renderer::initVulkan(ANativeWindow* window, int width, int height) {
    // In a real implementation, this would initialize Vulkan
    // For now, return false to fallback to GLES3
    return false;
}

void Renderer::renderScene() {
    // In a real implementation, this would render the scene objects
    // using the current view and projection matrices
}

void Renderer::renderVehicle(const VehicleState& state) {
    // Render the vehicle with the current state
    // This would use the GLTF loader to load and render the vehicle model
    // with appropriate transformations based on the physics state
}

void Renderer::renderTerrain() {
    // Render the terrain using the VBO from the terrain manager
    // This would draw the terrain mesh with appropriate LOD
}

void Renderer::renderUI() {
    // Render HUD overlays using the renderer
}