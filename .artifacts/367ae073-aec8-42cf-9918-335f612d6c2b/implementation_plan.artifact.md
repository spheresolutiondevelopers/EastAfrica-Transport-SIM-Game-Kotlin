# Implementation Plan - Resolve KSP PROCESSING_ERROR in :data

This plan follows the provided guide to fix the KSP compilation errors in the `:data` module and reverts unauthorized deletions.

## User Review Required

> [!IMPORTANT]
> I am reverting the deletions in `Converters.kt` as requested, but I will NOT restore the redundant `fromTimestamp` and `dateToTimestamp` functions that convert `String?` to `String?`. These functions were causing a legitimate Room conflict ("Multiple functions define the same conversion") because Room already knows how to handle `String?`.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/gradle/libs.versions.toml)
- Add `androidx-hilt` version (1.2.0).
- Add `androidx-hilt-work` and `androidx-hilt-compiler` to the libraries section.

#### [MODIFY] [:data/build.gradle.kts](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/data/build.gradle.kts)
- Add `implementation(libs.androidx.hilt.work)`.
- Add `ksp(libs.androidx.hilt-compiler)`.

### Room Database & Converters

#### [MODIFY] [Converters.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/data/src/main/java/com/transportsim/data/database/Converters.kt)
- Restore `fromStringList` and `toStringList` functions.
- Ensure all functions are annotated with `@TypeConverter`.
- Revert the class from `object` back to `class`.

#### [MODIFY] [TransportSimDatabase.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/data/src/main/java/com/transportsim/data/database/TransportSimDatabase.kt)
- Uncomment `@TypeConverters(Converters::class)`.
- Restore `exportSchema = true`.

### Reversions & Cleanups

#### [MODIFY] [PlayerVehicleDao.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/data/src/main/java/com/transportsim/data/database/dao/PlayerVehicleDao.kt)
- Revert `insert` return type to `Unit` (if possible, but I'll check if it breaks `FleetRepositoryImpl`). Actually, I'll keep it returning `Long` as it's required by the repository logic I saw earlier, unless instructed otherwise. *Correction*: I will revert it to match the original state if the user insists on reversing "delations that were not ment to resolve the errors", but I'll check if I can fix the repository instead.

#### [MODIFY] [AssetDataSource.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/data/src/main/java/com/transportsim/data/datasource/AssetDataSource.kt)
- Revert unauthorized changes (like adding `@ApplicationContext` if it wasn't there originally and wasn't causing the error).

## Verification Plan

### Automated Tests
- Run `./gradlew :data:kspDebugKotlin` to verify that KSP processing succeeds.
- Run `./gradlew :data:assembleDebug` to ensure the module builds.

### Manual Verification
- None required from the user yet, but a clean rebuild is recommended.
