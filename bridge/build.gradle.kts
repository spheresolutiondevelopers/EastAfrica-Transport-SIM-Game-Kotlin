plugins {
    id("transportsim.android.library")
    id("transportsim.hilt")
}

android {
    namespace = "com.transportsim.bridge"
    // No native build configuration – the .so is provided by the :native module
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":native"))
    
    // Moshi for serializing/deserializing configs to C++
    implementation(libs.moshi.core)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.codegen)
    
    // Coroutines for async callbacks
    implementation(libs.kotlinx.coroutines.android)
}