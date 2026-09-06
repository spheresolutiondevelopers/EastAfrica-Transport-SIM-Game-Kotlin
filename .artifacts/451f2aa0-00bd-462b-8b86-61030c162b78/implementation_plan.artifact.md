# Implementation Plan - UI Refinements for Dashboard and Fleet

This plan addresses two UI issues:
1.  **3D Vehicle Turntable Centering**: Vehicles are currently appearing at the top of the display area instead of being centered.
2.  **Vehicle Category Ordering**: Reorder vehicle category cards in the Fleet sidebar (Dashboard) and Fleet screen to start with Pickup and Taxi, moving Bus and Matatu to the bottom.

## Proposed Changes

### [Component Name] Domain Models

#### [MODIFY] [VehicleCategory.kt](file:///C:/Users/lenovo/Projects/Transport%20simulation/TransportsimAndroid/domain/src/main/java/com/transportsim/domain/models/VehicleCategory.kt)
- Reorder `VehicleCategory` enum members to: `PICKUP`, `TAXI`, `LORRY`, `BODA`, `BUS`, `MATATU`. This will affect the "natural order" used in lists and sorting.

### [Component Name] Dashboard UI

#### [MODIFY] [VehicleTurntable.kt](file:///C:/Users/lenovo/Projects/Transport%20simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/dashboard/components/VehicleTurntable.kt)
- Update `ModelNode` instantiation to use `centerOrigin = Position(0f, 0f, 0f)`.
- Import `io.github.sceneview.math.Position`.

#### [MODIFY] [DashboardScreen.kt](file:///C:/Users/lenovo/Projects/Transport%20simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/dashboard/DashboardScreen.kt)
- Change the initial `selectedCategory` default from `BUS` to `PICKUP`.

#### [MODIFY] [DashboardViewModel.kt](file:///C:/Users/lenovo/Projects/Transport%20simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/dashboard/DashboardViewModel.kt)
- Change initial category selection logic to use `PICKUP` instead of `BUS`.

### [Component Name] Fleet UI

#### [MODIFY] [FleetScreen.kt](file:///C:/Users/lenovo/Projects/Transport%20simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/fleet/FleetScreen.kt)
- Ensure the `groupedVehicles` map is sorted by the enum order. Use `toSortedMap()` on the grouped result.

## Verification Plan

### Automated Tests
- N/A (UI changes)

### Manual Verification
- **Dashboard**:
    - Verify the 3D vehicle in the turntable is centered vertically within its Box.
    - Verify the left "Fleet" sidebar lists categories in the order: Pickup, Taxi, Lorry, Boda, Bus, Matatu.
    - Verify Pickup is selected by default.
- **Fleet Screen**:
    - Navigate to the Fleet screen and verify the category sections follow the new order.
