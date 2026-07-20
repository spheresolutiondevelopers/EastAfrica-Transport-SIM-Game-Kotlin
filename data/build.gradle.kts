plugins {
    id("transportsim.android.library")
    id("transportsim.hilt")
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
    implementation(libs.androidx.security.crypto)
    implementation(libs.datastore.encryption)

    // WorkManager
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // Moshi (JSON parsing)
    implementation(libs.moshi.core)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.codegen)

    // Coroutines (already in library convention)
    // Hilt (already in Hilt convention)

    // Testing
    testImplementation(libs.kotlinx.coroutines.testing)
    androidTestImplementation(libs.androidx.room.testing)
}