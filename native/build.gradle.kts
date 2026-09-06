plugins {
    id("transportsim.native")
}

android {
    namespace = "com.transportsim.native_lib"
    
    defaultConfig {
        externalNativeBuild {
            cmake {
                cppFlags("-std=c++17 -frtti -fexceptions -O3 -DNDEBUG")
                arguments("-DANDROID_STL=c++_shared", "-DCMAKE_SHARED_LINKER_FLAGS=-Wl,-z,max-page-size=16384")
            }
        }
        ndk {
            abiFilters.add("arm64-v8a")
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }
    
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
}

// The native module produces a shared library (.so) that gets packaged into the APK