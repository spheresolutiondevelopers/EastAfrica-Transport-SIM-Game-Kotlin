#pragma once
#include <jni.h>
#include <string>

class GLTFLoader {
public:
    GLTFLoader();
    ~GLTFLoader();

    bool init(jobject assetManager);
    bool loadModel(const std::string& path);
};
