plugins {
    id("transportsim.android.application")
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.transportsim.benchmark"
    defaultConfig {
        applicationId = "com.transportsim.benchmark"
        // Min SDK must be high enough for benchmark; typically 21+
        minSdk = 21
        targetSdk = 35
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

dependencies {
    implementation(project(":app"))
    implementation(libs.androidx.benchmark.macro)
    androidTestImplementation(libs.androidx.benchmark.macro)
}