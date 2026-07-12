plugins {
    id("transportsim.android.library")
    id("transportsim.hilt")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.transportsim.data"
    // The defaultConfig minSdk is inherited from the convention plugin
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":bridge")) // For JNI callbacks (optional)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // SQLCipher (encrypted Room)
    implementation(libs.sqlcipher.android)
    implementation(libs.androidx.sqlite)

    // DataStore (encrypted)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.security.crypto.datastore)

    // WorkManager
    implementation(libs.androidx.work.runtime)

    // Moshi (JSON parsing)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.codegen)

    // Coroutines (already in library convention)
    // Hilt (already in Hilt convention)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.room.testing)
}