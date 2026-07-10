import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

class NativeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            apply(plugin = "com.android.library")

            extensions.configure<LibraryExtension> {
                // The native module is a library that produces .so files
                // It doesn't need a defaultConfig minSdk etc.
                compileSdk = 35
                defaultConfig {
                    minSdk = 26
                    externalNativeBuild {
                        cmake {
                            cppFlags("-std=c++17 -frtti -fexceptions")
                            arguments("-DANDROID_STL=c++_shared")
                        }
                    }
                    ndk {
                        // Only build for arm64-v8a to reduce APK size
                        abiFilters.add("arm64-v8a")
                    }
                }
                externalNativeBuild {
                    cmake {
                        path = file("src/main/cpp/CMakeLists.txt")
                    }
                }
                // Disable all non-native build features
                buildFeatures {
                    compose = false
                    buildConfig = false
                }
            }
        }
    }
}