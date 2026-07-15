#pragma once
#include <jni.h>

class TextureManager {
public:
    TextureManager();
    ~TextureManager();

    bool init(jobject assetManager);
};
