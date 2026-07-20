# Implementation Plan - Fleet Screen

Implement a comprehensive Fleet Screen that displays both owned and available vehicles from a static catalog of 30 vehicles (6 categories × 5 variants).

## User Review Required

> [!IMPORTANT]
> The implementation involves adding a new assets file and updating multiple layers (Domain, Data, UI).
> It also changes the logic from showing only owned vehicles to showing the entire catalog with ownership status.

## Proposed Changes

### [Component Name] Domain

#### [NEW] [FleetVehicle.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/domain/src/main/java/com/transportsim/domain/models/FleetVehicle.kt)
Create a new domain model that combines `VehicleCatalogEntry` with optional `Vehicle` (ownership info).

### [Component Name] Data

#### [NEW] [vehicle_specs.json](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/assets/config/vehicle_specs.json)
Add the full 30-vehicle catalog in JSON format.

#### [MODIFY] [FleetRepository.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/domain/src/main/java/com/transportsim/domain/repositories/FleetRepository.kt)
Update interface to return `FleetVehicle` and include `getFullFleet()`.

#### [MODIFY] [FleetRepositoryImpl.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/data/src/main/java/com/transportsim/data/repository/FleetRepositoryImpl.kt)
Update implementation to join catalog and ownership data.

### [Component Name] UI

#### [MODIFY] [FleetViewModel.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/fleet/FleetViewModel.kt)
Update to use `FleetVehicle`, handle filtering/sorting on the combined list, and implement `purchaseVehicle`.

#### [MODIFY] [FleetScreen.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/fleet/FleetScreen.kt)
Update to display the full fleet grid, handling loading and empty states.

#### [MODIFY] [FleetVehicleCard.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/fleet/components/FleetVehicleCard.kt)
Update to reflect ownership status (Owned vs Locked) and show appropriate actions (Deploy/Service vs Purchase).

## Verification Plan

### Automated Tests
- Build the project to ensure no syntax errors.
- (Optional) Add a unit test for `FleetRepositoryImpl.getFullFleet()` if infrastructure allows.

### Manual Verification
- Deploy to an Android device/emulator.
- Navigate to the Fleet screen.
- Verify 30 vehicles are displayed.
- Verify owned vehicles show "Owned" badge and "Deploy/Service" buttons.
- Verify locked vehicles show "Locked" badge and "Purchase" button with price.
- Test category filtering and sorting.
