plugins {
    id("transportsim.android.library")
    id("transportsim.hilt")
}

android {
    namespace = "com.transportsim.billing"
}

dependencies {
    implementation(project(":domain"))
    
    // Play Billing Library
    implementation(libs.play.billing)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
}
