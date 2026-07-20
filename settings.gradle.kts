pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        // versions are resolved via version catalog – see gradle/libs.versions.toml
        id("com.android.application") version "9.2.1"
        id("com.android.library") version "9.2.1"
        id("org.jetbrains.kotlin.android") version "2.1.0"
        id("com.google.dagger.hilt.android") version "2.60.1"
        id("com.google.devtools.ksp") version "2.1.0-1.0.29"
        id("com.android.asset-pack") version "9.2.1"
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
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