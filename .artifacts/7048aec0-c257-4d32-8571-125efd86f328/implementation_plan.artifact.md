# Implementation Plan - Enforce JDK 21 via Toolchains

This plan enforces the use of JDK 21 for all modules in the multi-modular project using Gradle Toolchains, addressing the discrepancy with the system JDK 26.

## User Review Required

> [!NOTE]
> We will use Gradle Toolchains instead of deleting the system JDK. This is the standard Android/Gradle way to handle multiple Java versions.
> Gradle will automatically look for JDK 21 or download it if necessary (using the Foojay resolver already present in the project).

## Proposed Changes

### Build Logic (Conventions)

#### [MODIFY] [KotlinJvmConventionPlugin.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/build-logic/conventions/src/main/kotlin/KotlinJvmConventionPlugin.kt)
- Add `jvmToolchain(21)` to the `KotlinJvmProjectExtension`.

#### [MODIFY] [AndroidApplicationConventionPlugin.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/build-logic/conventions/src/main/kotlin/AndroidApplicationConventionPlugin.kt)
- Add `jvmToolchain(21)` to the `KotlinAndroidProjectExtension`.

#### [MODIFY] [AndroidLibraryConventionPlugin.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/build-logic/conventions/src/main/kotlin/AndroidLibraryConventionPlugin.kt)
- Add `jvmToolchain(21)` to the `KotlinAndroidProjectExtension`.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:compileDebugKotlin` and check the build logs to ensure it uses JDK 21 for compilation.
- Verify that the `jvmTarget` remains consistent across all modules.

### Manual Verification
- Run `./gradlew javaToolchains` to see the detected and used toolchains.
