# Implementation Plan - Enhanced 3D Turntable and Build Optimizations

Refine the 3D vehicle turntable to support category-based navigation, status display (Active/Owned/Locked), and address build warnings/asset inclusion.

## User Review Required

> [!IMPORTANT]
> - I will modify `DashboardUiState` to include `categoryVehicles` and `selectedTurntableIndex`.
> - `VehicleTurntable` will now feature navigation arrows and a status bar.
> - I will update `app/build.gradle.kts` to correctly link the `:game_assets` asset pack.

## Proposed Changes

### Build & Assets

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/build.gradle.kts)
- Add `assetPacks.add(":game_assets")` to the `android` block to ensure assets are correctly bundled.
- Add 16KB page size alignment configuration to the `packaging` block.

#### [MODIFY] [native/build.gradle.kts](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/native/build.gradle.kts)
- Add 16KB page size alignment flags to CMake and packaging options to resolve the "16kb warning".

### Dashboard Logic

#### [MODIFY] [DashboardModels.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/dashboard/models/DashboardModels.kt)
- Update `DashboardUiState` to include:
    - `categoryVehicles: List<FleetVehicle>`
    - `selectedTurntableIndex: Int`

#### [MODIFY] [DashboardViewModel.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/dashboard/DashboardViewModel.kt)
- Implement `selectCategory(category: VehicleCategory)` to update `categoryVehicles`.
- Implement `nextTurntableVehicle()` and `previousTurntableVehicle()` to cycle through the 5 variants.
- Update `loadDashboardData` to initialize the turntable with the first category's vehicles.

### UI Enhancements

#### [MODIFY] [VehicleTurntable.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/dashboard/components/VehicleTurntable.kt)
- Add navigation arrows (Left/Right) to switch between the 5 vehicles.
- Implement a status bar showing:
    - **ACTIVE**: If it's the currently used vehicle.
    - **PURCHASED**: If owned but not active.
    - **LOCKED**: If not yet purchased.
- Update the 3D model loading logic to use the `assetFileBase` from the `FleetVehicle`.

#### [MODIFY] [DashboardCenter.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/dashboard/components/DashboardCenter.kt)
- Update `DashCenter` to pass navigation callbacks and the current `FleetVehicle` to the turntable.

#### [MODIFY] [DashboardScreen.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/dashboard/DashboardScreen.kt)
- Connect category selection and turntable navigation to the ViewModel.

## Verification Plan

### Automated Tests
- Build the project to verify that the 16KB warning is resolved.
- Verify asset inclusion by checking build logs (or requesting user to check APK size).

### Manual Verification
- Navigate through categories (Taxi, Bus, etc.) and verify the turntable updates.
- Use arrows to cycle through the 5 vehicles in a category.
- Verify status display (Locked/Owned/Active) updates correctly.
