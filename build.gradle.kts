// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    // This is a convention plugin, but we apply it to all modules via the root project.
    // Instead we rely on each module applying its own convention plugin.
    // We keep this empty but ensure all subprojects use the convention plugins.
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}