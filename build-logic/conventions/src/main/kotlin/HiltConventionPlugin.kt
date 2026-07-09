import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.dagger.hilt.android")
                apply("org.jetbrains.kotlin.kapt") // optional, but Hilt still needs kapt for compiler
                // Alternatively, use KSP for Hilt: apply("com.google.devtools.ksp")
                // We'll use kapt for compatibility.
            }

            dependencies {
                "implementation"(libs.hilt.android)
                "kapt"(libs.hilt.compiler)
                // For instrumentation tests
                "androidTestImplementation"(libs.hilt.android)
                "kaptAndroidTest"(libs.hilt.compiler)
            }
        }
    }
}