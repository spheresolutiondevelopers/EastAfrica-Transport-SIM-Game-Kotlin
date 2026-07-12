plugins {
    id("transportsim.android.library")
    id("transportsim.hilt")
}

android {
    namespace = "com.transportsim.bridge"
    
    defaultConfig {
        externalNativeBuild {
            cmake {
                cppFlags("-std=c++17 -frtti -fexceptions")
                arguments("-DANDROID_STL=c++_shared")
            }
        }
        ndk {
            abiFilters("arm64-v8a")
        }
    }
    
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":native"))
    
    // Moshi for serializing/deserializing configs to C++
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.codegen)
    
    // Coroutines for async callbacks
    implementation(libs.kotlinx.coroutines.android)
}