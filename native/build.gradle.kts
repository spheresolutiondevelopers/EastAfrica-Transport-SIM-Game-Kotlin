plugins {
    id("transportsim.native")
}

android {
    namespace = "com.transportsim.native"
    
    defaultConfig {
        externalNativeBuild {
            cmake {
                cppFlags("-std=c++17 -frtti -fexceptions -O3 -DNDEBUG")
                arguments(
                    "-DANDROID_STL=c++_shared",
                    "-DCMAKE_BUILD_TYPE=Release"
                )
            }
        }
    }
    
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
}

// The native module produces a shared library (.so) that gets packaged into the APK