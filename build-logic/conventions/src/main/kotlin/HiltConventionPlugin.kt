import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import com.google.devtools.ksp.gradle.KspExtension

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            apply(plugin = "com.google.dagger.hilt.android")
            apply(plugin = "com.google.devtools.ksp")

            dependencies {
                add("implementation", libs.findLibrary("hilt-android").get())
                add("ksp", libs.findLibrary("hilt-compiler").get())
                add("androidTestImplementation", libs.findLibrary("hilt-android").get())
                add("androidTestImplementation", libs.findLibrary("hilt-compiler").get())
            }

            extensions.configure<KspExtension> {
                arg("dagger.hilt.disableModulesHaveInstallInCheck", "true")
            }
        }
    }
}
