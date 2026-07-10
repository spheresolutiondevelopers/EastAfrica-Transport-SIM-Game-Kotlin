import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            apply(plugin = "com.google.dagger.hilt.android")
            apply(plugin = "com.google.devtools.ksp")

            // Add Hilt dependencies
            dependencies {
                add("implementation", libs.findLibrary("hilt-android").get())
                add("ksp", libs.findLibrary("hilt-compiler").get())
                // For instrumentation tests
                add("androidTestImplementation", libs.findLibrary("hilt-android").get())
                add("androidTestImplementation", libs.findLibrary("hilt-compiler").get())
            }

            // Hilt Java compiler args
            extensions.getByType<com.google.devtools.ksp.gradle.KspExtension>().apply {
                arg("dagger.hilt.disableModulesHaveInstallInCheck", "true")
            }
        }
    }
}