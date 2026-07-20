# Resolve Build Errors in :app module

The `:app` module has numerous compilation errors including missing WorkManager implementation, missing domain/compose imports, and incomplete UI files.

## Proposed Changes

### [Component] :app module

#### [MODIFY] [TransportsimApplication.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimApplication.kt)
- Fix `workManagerConfiguration` to be an `override val` instead of `getWorkManagerConfiguration()` method to match WorkManager 2.9+ API.

#### [MODIFY] [AppState.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/app/AppState.kt)
- Mark `shouldShowBottomBar()` as `@Composable` since it accesses `@Composable` properties.

#### [MODIFY] [DashboardViewModel.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/dashboard/DashboardViewModel.kt)
- Add `MapVehicle` data class.
- Add `mapVehicles: List<MapVehicle>` to `DashboardUiState`.
- Initialize `mapVehicles` in `loadDashboardData()`.

#### [MODIFY] [DashboardScreen.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/dashboard/DashboardScreen.kt)
- Add missing imports for `Vehicle`, `VehicleCategory`, `Mission`, `MissionStatus`, etc. from `com.transportsim.domain.models`.
- Add missing Compose imports if needed.

#### [MODIFY] [AppNavHost.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/app/AppNavHost.kt)
- Fix imports for `MissionDetailScreen` (it's in `.components` package).
- Create stubs for missing screens: `FleetDetailScreen`, `GarageDetailScreen`.

#### [MODIFY] [SimulationViewModel.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/simulation/SimulationViewModel.kt)
- Fix access to `nativeCallbacks` (check if it should be public in `SimulationSession` or accessed differently).
- Fix `stepPhysics` reference.
- Add missing imports for `Color`, `Gold`, etc.

#### [FIX] Miscellaneous UI Errors
- Resolve conflicting overloads in `CategoryProgressBar.kt` and `LevelProgressCard.kt`.
- Fix exhaustive `when` in `CategoryGrid.kt` and `MapView.kt`.
- Fix `@Composable` context issues in `MapView.kt`.

#### [NEW] [GarageDetailScreen.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/garage/GarageDetailScreen.kt)
- Create a stub implementation for `GarageDetailScreen`.

#### [MODIFY] [FleetDetailScreen.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/fleet/FleetDetailScreen.kt)
- Add a stub implementation since the file is currently empty.

## Verification Plan

### Automated Tests
- Run `:app:assembleDebug` to verify all compilation errors are resolved.
