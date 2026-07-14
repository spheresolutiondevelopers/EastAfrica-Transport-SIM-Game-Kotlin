plugins {
    id("transportsim.kotlin.jvm")
    alias(libs.plugins.kotlin.serialization)
}

// No Android-specific configuration – pure Kotlin module

dependencies {
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)

    // Serialization
    implementation(libs.kotlinx.serialization.json)
    
    // Testing
    testImplementation(libs.junit)
}
