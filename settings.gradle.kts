pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        // versions are resolved via version catalog – see gradle/libs.versions.toml
        id("com.android.application") version "8.5.0"
        id("com.android.library") version "8.5.0"
        id("org.jetbrains.kotlin.android") version "2.0.20"
        id("com.google.dagger.hilt.android") version "2.55"
        id("com.google.devtools.ksp") version "2.0.20-1.0.25"
        id("com.google.android.play.assetpack") version "2.0.1"
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "TransportsimAndroid"
include(
    ":app",
    ":domain",
    ":data",
    ":bridge",
    ":native",
    ":billing",
    ":game_assets",
    ":benchmark"
)
// build-logic is a included build for convention plugins
includeBuild("build-logic")