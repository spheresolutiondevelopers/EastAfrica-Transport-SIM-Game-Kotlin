 plugins {
    id("transportsim.android.application")
    id("transportsim.hilt")
}

android {
    namespace = "com.transportsim.app"
    
    defaultConfig {
        applicationId = "com.transportsim.app"
        versionCode = 1
        versionName = "1.0.0" 
        
        // Enable vector drawables for backward compatibility
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    
    // Enable build configuration for feature flags
    buildFeatures {
        buildConfig = true
    }

    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":bridge"))
    implementation(project(":billing"))
    implementation(project(":game_assets"))
    
    // Compose (provided by convention plugin)
    // Hilt (provided by convention plugin)
    
    // Navigation Compose (already in convention plugin)
    
    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.7.0")
    
    // Accompanist for utilities
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.35.0-alpha")
    implementation("com.google.accompanist:accompanist-permissions:0.35.0-alpha")

    // SceneView for 3D rendering
    implementation(libs.sceneview)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // --- Hilt & WorkManager Integration ---
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
}