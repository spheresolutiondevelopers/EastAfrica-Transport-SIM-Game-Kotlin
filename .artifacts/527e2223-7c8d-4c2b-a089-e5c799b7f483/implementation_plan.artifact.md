# Fix Build Errors in TransportsimAndroid

The project currently has numerous compilation errors ranging from missing imports to type mismatches and incorrect API usage in Jetpack Compose.

## Proposed Changes

### 1. Fix Missing Imports and Simple Symbol Issues
Many files are missing standard Compose imports like `sp`, `Alignment`, `Color`, `Box`, `tween`, `MaterialTheme`, `RoundedCornerShape`, `clip`, `FontWeight`.

Files involved:
- `CategoryProgressBar.kt`
- `GarageVehicleList.kt`
- `LeaderboardCard.kt`
- `TodayStatsCard.kt`
- `BoardingOverlay.kt`
- `BusStopPrompt.kt`
- `CollisionFlash.kt`
- `KeyHints.kt`
- `OverspeedWarning.kt`
- `ScorePopup.kt`
- `SimulationLoadingOverlay.kt`
- `SimulationMiniMap.kt`
- `SimulationTopHud.kt`
- `TrafficLightIndicator.kt`
- `TrainingHeroCard.kt`
- `TrainingProgressCard.kt`
- `TrainingScenarioCard.kt`

### 2. Fix UI Component API Usage
- `SettingsTextField.kt`: Fix `outlinedTextFieldColors` (likely moved or renamed in M3).
- `FleetFilterBar.kt`: Fix `FilterChip` parameter `selected`.
- `RewardTrackCard.kt`: Fix `Text` call parameters.

### 3. Fix Logic and Type Mismatches
- `VehicleTurntable.kt`: Fix return type in a lambda or function.
- `GarageInventoryPanel.kt`: Fix type mismatch (String vs Int).
- `SimulationScreen.kt`: Fix nullability issue (Int? vs Int).
- `DashboardScreen.kt`: Resolve `mapVehicles`.
- `DashboardViewModel.kt`: Resolve `isUnlocked`.
- `SimulationGLSurfaceView.kt`: Resolve `renderFrame`.

### 4. Fix Complex Simulation UI Logic
- `SimulationBottomHud.kt`: Fix `nativeCanvas` usage, `drawText` issues, re-assignment of `val`, and Composable calls inside `DrawScope`.
- `SimulationControls.kt`: Fix `detectDragGestures` and `Alignment` usage.
- `SimulationMiniMap.kt`: Fix `drawLine` and colors.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` after each batch of fixes to verify progress.
- Finally, ensure a clean build with `./gradlew assembleDebug`.

### Manual Verification
- Deploy the app to a device or emulator to ensure the UI renders correctly and the simulation functions as expected.
