plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.android.gradle.plugin)
    implementation(libs.hilt.gradle.plugin)
    implementation(libs.ksp.gradle.plugin)
}

gradlePlugin {
    plugins {
        create("androidApplication") {
            id = "transportsim.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        create("androidLibrary") {
            id = "transportsim.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        create("hilt") {
            id = "transportsim.hilt"
            implementationClass = "HiltConventionPlugin"
        }
        create("kotlinJvm") {
            id = "transportsim.kotlin.jvm"
            implementationClass = "KotlinJvmConventionPlugin"
        }
        create("native") {
            id = "transportsim.native"
            implementationClass = "NativeConventionPlugin"
        }
        create("assetPack") {
            id = "transportsim.asset.pack"
            implementationClass = "AssetPackConventionPlugin"
        }
    }
}