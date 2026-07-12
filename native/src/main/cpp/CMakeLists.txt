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
find_package(OpenGLES REQUIRED)
find_package(EGL REQUIRED)

# Add the source files
set(SOURCES
    # JNI bindings
    jni/native_glue.cpp
    jni/transportsim_jni.cpp
    
    # Physics engine
    physics/PhysicsEngine.cpp
    
    # Terrain system
    terrain/TerrainManager.cpp
    terrain/TerrainChunk.cpp
    
    # Renderer
    renderer/Renderer.cpp
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

# For ARM64, enable NEON optimizations
if(ANDROID_ABI STREQUAL "arm64-v8a")
    target_compile_options(transportsim_native PRIVATE -mfpu=neon)
endif()

# Create a .so file that is stripped for release
set_target_properties(transportsim_native PROPERTIES
    VERSION 1
    SOVERSION 1
    CXX_STANDARD_REQUIRED ON
    CXX_EXTENSIONS OFF
)