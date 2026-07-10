import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

class AssetPackConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            apply(plugin = "com.android.library")
            apply(plugin = "com.google.android.play.assetpack")

            extensions.configure<LibraryExtension> {
                compileSdk = 35
                // This is an asset pack, no code is compiled
                // It only contains assets
                buildFeatures {
                    compose = false
                    buildConfig = false
                }
            }
        }
    }
}