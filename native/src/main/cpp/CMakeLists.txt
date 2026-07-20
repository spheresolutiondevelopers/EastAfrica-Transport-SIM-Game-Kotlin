cmake_minimum_required(VERSION 3.22)
project(transportsim_native)

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# Enable verbose output for debugging
set(CMAKE_VERBOSE_MAKEFILE ON)

# Set the output directory for the library
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})

# Find required packages
include(FetchContent)

# nlohmann/json
FetchContent_Declare(
    json
    URL https://github.com/nlohmann/json/releases/download/v3.11.3/json.tar.xz
)
FetchContent_MakeAvailable(json)

# Eigen (Bypass internal tests/benchmarks by manually defining the interface library)
FetchContent_Declare(
    eigen
    URL https://gitlab.com/libeigen/eigen/-/archive/3.4.0/eigen-3.4.0.tar.gz
)
FetchContent_GetProperties(eigen)
if(NOT eigen_POPULATED)
    FetchContent_Populate(eigen)
    # Manually define the Eigen target instead of add_subdirectory
    # This prevents CMake from building Eigen's tests and benchmarks
    add_library(eigen_manual INTERFACE)
    add_library(Eigen3::Eigen ALIAS eigen_manual)
    target_include_directories(eigen_manual INTERFACE ${eigen_SOURCE_DIR})
endif()

# Add the source files
set(SOURCES
    # JNI bindings
    ${CMAKE_CURRENT_SOURCE_DIR}/jni/native_glue.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/jni/callback_bridge.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/transportsim_jni.cpp
    
    # Physics engine
    ${CMAKE_CURRENT_SOURCE_DIR}/physics/PhysicsEngine.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/physics/Pacejka.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/physics/Ackermann.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/physics/Suspension.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/physics/VehicleDynamics.cpp
    
    # Terrain system
    ${CMAKE_CURRENT_SOURCE_DIR}/terrain/TerrainManager.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/terrain/TerrainChunk.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/terrain/TerrainLoader.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/terrain/FrictionTable.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/terrain/Interpolation.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/terrain/VBOArena.cpp
    
    # Renderer
    ${CMAKE_CURRENT_SOURCE_DIR}/renderer/Renderer.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/renderer/GLTFLoader.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/renderer/ShaderManager.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/renderer/TextureManager.cpp
)

# Create shared library
add_library(transportsim_native SHARED ${SOURCES})

# Include directories
target_include_directories(transportsim_native PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}
    ${CMAKE_CURRENT_SOURCE_DIR}/physics
    ${CMAKE_CURRENT_SOURCE_DIR}/terrain
    ${CMAKE_CURRENT_SOURCE_DIR}/renderer
    ${CMAKE_CURRENT_SOURCE_DIR}/jni
)

# Link libraries
target_link_libraries(transportsim_native
    log
    android
    GLESv3
    EGL
    c++_shared
    m
    nlohmann_json::nlohmann_json
    Eigen3::Eigen
)

# Define preprocessor macros
target_compile_definitions(transportsim_native PRIVATE
    -DGLM_FORCE_RADIANS
    -DGLM_FORCE_DEPTH_ZERO_TO_ONE
)

# Set compilation flags
target_compile_options(transportsim_native PRIVATE
    -O3
    -DNDEBUG
    -fno-rtti
    -fno-exceptions
)

# For ARM64, NEON is always available, -mfpu=neon is not needed and not supported by Clang for aarch64
# For ARMv7, it might be needed, but this project seems to target arm64-v8a primarily
if(ANDROID_ABI STREQUAL "armeabi-v7a")
    target_compile_options(transportsim_native PRIVATE -mfpu=neon)
endif()

# Create a .so file that is stripped for release
set_target_properties(transportsim_native PROPERTIES
    VERSION 1
    SOVERSION 1
    CXX_STANDARD_REQUIRED ON
    CXX_EXTENSIONS OFF
)

# Align to 16 KB boundaries for Android 15+ compatibility
target_link_options(transportsim_native PRIVATE "-Wl,-z,max-page-size=16384")
