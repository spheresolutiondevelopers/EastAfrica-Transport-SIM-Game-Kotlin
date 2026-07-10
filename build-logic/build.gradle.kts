plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

dependencies {
    // We need the Android Gradle Plugin and Kotlin Gradle Plugin to write convention plugins
    implementation(libs.android.gradlePlugin) // will be added via version catalog
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.hilt.gradlePlugin)
    implementation(libs.ksp.gradlePlugin)
    implementation(libs.assetPack.gradlePlugin)
}

// Ensure the version catalog is accessible from the convention plugins
// (it's already accessible via the libs extension)