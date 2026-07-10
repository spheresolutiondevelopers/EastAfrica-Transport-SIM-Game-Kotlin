plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

// This module publishes the convention plugins
dependencies {
    // Make the version catalog available to the plugins
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
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
            id = "transportsim.assetpack"
            implementationClass = "AssetPackConventionPlugin"
        }
    }
}