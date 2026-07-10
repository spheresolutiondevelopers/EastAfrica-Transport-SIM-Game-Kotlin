plugins {
    id("transportsim.kotlin.jvm")
}

// No Android-specific configuration – pure Kotlin module

dependencies {
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    
    // Testing
    testImplementation(libs.junit)
}