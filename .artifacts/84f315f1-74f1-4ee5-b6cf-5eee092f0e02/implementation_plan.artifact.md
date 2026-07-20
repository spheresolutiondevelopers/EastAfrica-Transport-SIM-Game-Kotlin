# Implementation Plan - Fix Hilt Metadata Version Error

The project is failing to build with a `java.lang.IllegalArgumentException: Provided Metadata instance has version 2.2.0, while maximum supported version is 2.1.0`. This is caused by an incompatibility between the Kotlin version used and the Hilt version (2.55). Upgrading Hilt to a version that "unshades" `kotlinx-metadata-jvm` (2.57+) or a newer version (2.60.1) should resolve this.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/gradle/libs.versions.toml)
- Update `hilt` version from `2.55` to `2.60.1`.

#### [MODIFY] [settings.gradle.kts](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/settings.gradle.kts)
- Update Hilt plugin version from `2.55` to `2.60.1`.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:hiltJavaCompileDebug` to verify the fix for the specific failing task.
- Run a full build: `./gradlew assembleDebug`.

### Manual Verification
- Perform a Gradle Sync in Android Studio.
- Verify that Hilt components are still correctly generated and recognized by the IDE.
