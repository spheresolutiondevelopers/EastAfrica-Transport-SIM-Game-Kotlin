plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

dependencies {
}

// Ensure the version catalog is accessible from the convention plugins
// (it's already accessible via the libs extension)