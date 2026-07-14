import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class NativeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            apply(plugin = "com.android.library")

            extensions.configure<LibraryExtension> {
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
                        abiFilters.add("arm64-v8a")
                    }
                }
                externalNativeBuild {
                    cmake {
                        path = file("src/main/cpp/CMakeLists.txt")
                    }
                }
                buildFeatures {
                    compose = false
                    buildConfig = false
                }
            }
        }
    }
}
