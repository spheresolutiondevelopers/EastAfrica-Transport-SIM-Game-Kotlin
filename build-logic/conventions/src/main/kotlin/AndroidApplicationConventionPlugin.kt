import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<AppExtension> {
                compileSdk = 35
                defaultConfig {
                    minSdk = 26
                    targetSdk = 35
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
                kotlinOptions {
                    jvmTarget = "17"
                }
                buildFeatures {
                    buildConfig = true
                    compose = true
                }
                composeOptions {
                    kotlinCompilerExtensionVersion = "1.5.4" // matches Compose BOM
                }
            }

            dependencies {
                "implementation"(platform(libs.androidx.compose.bom))
                "implementation"(libs.androidx.compose.ui)
                "implementation"(libs.androidx.compose.ui.tooling)
                "implementation"(libs.androidx.compose.ui.tooling.preview)
                "implementation"(libs.androidx.compose.material3)
                "implementation"(libs.androidx.compose.material.icons)
                "implementation"(libs.androidx.navigation.compose)
                "implementation"(libs.androidx.hilt.navigation.compose)
                "implementation"(libs.androidx.lifecycle.compose)
                "implementation"(libs.androidx.activity.compose)
            }
        }
    }
}