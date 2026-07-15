#pragma once
#include <jni.h>

class ShaderManager {
public:
    ShaderManager();
    ~ShaderManager();

    bool init(jobject assetManager, bool useVulkan);
};
