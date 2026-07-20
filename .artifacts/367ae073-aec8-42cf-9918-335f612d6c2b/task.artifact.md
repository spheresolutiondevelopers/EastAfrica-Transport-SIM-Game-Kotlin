# Tasks - Fix :data KSP Errors

- [ ] Update `gradle/libs.versions.toml` with Hilt-Work dependencies
- [ ] Update `:data/build.gradle.kts` with Hilt-Work and KSP compiler
- [ ] Fix `Converters.kt`: Restore missing functions and add `@TypeConverter`
- [ ] Fix `TransportSimDatabase.kt`: Restore `@TypeConverters` and `exportSchema`
- [ ] Revert unauthorized changes in `AssetDataSource.kt`
- [ ] Revert unauthorized changes in `PlayerVehicleDao.kt` (checking for regressions)
- [ ] Verify build with `./gradlew :data:kspDebugKotlin`
