# Dashboard Design Match (HTML Alignment)

The current Android dashboard implementation does not match the provided high-fidelity HTML design (`transportsim_v3_5-22.html`). This plan details the steps to align the Android app's dashboard with the HTML design, including colors, typography, and the three-column responsive layout.

## User Review Required

> [!IMPORTANT]
> **Typography**: I will be using Google Fonts (`ui-text-google-fonts`) to fetch Rajdhani, Orbitron, and Share Tech Mono. This requires an internet connection on the device/emulator for the first run.
>
> **Layout Strategy**: The HTML design is primarily optimized for landscape. I will implement a responsive layout that uses a three-column structure in landscape and a scrollable single-column structure in portrait (with a navigation drawer as per HTML).

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/gradle/libs.versions.toml)
- Add `androidx-compose-ui-google-fonts` dependency.

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/build.gradle.kts)
- Add `implementation(libs.androidx.compose.ui.google.fonts)` to the dependencies.

### Design System (Theme)

#### [MODIFY] [Color.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/theme/Color.kt)
- Define the exact HEX colors from the HTML `:root` (e.g., `BgDeep`, `AccentCyan`, `TextPri`).

#### [MODIFY] [Type.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/theme/Type.kt)
- Configure `Rajdhani` (Body/General), `Orbitron` (Headlines/Stats), and `Share Tech Mono` (Badges/Time) using Google Fonts.

#### [MODIFY] [Theme.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/theme/Theme.kt)
- Update `DarkColorScheme` to use the new colors.
- Ensure the background gradient effect from HTML can be applied to screens.

### Dashboard Feature

#### [MODIFY] [DashboardScreen.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/dashboard/DashboardScreen.kt)
- Implement a custom `DashboardHUD` component to replace the standard `TopAppBar`.
- Refactor the main content to use a `Box` with a responsive `Row` (landscape) or `Column` (portrait).
- Group UI elements into `FleetColumn`, `MapColumn`, and `StatsColumn`.

#### [NEW] [DashboardComponents.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/dashboard/components/DashboardComponents.kt)
- Create smaller, reusable components that match HTML design:
    - `MiniStatCard`: High-tech stats card with change indicators.
    - `VehicleCategoryCard`: The left-side fleet cards with custom progress bars.
    - `HUDStatItem`: Individual stat blocks in the top bar.
    - `RouteStatusList`: The list of live routes.

#### [MODIFY] [DashboardViewModel.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/dashboard/DashboardViewModel.kt)
- Add data fields to `DashboardUiState` to support HUD stats (Balance, Total Fleet, Active Vehicles).

## Verification Plan

### Automated Tests
- Run `gradlew :app:assembleDebug` to ensure no build errors after theme changes.

### Manual Verification
- **Visual Check**: Compare side-by-side screenshots of the Android Dashboard and the provided HTML design.
- **Responsiveness**: Rotate the device to verify that the three-column layout correctly transitions between landscape and portrait.
- **Typography**: Verify that Rajdhani, Orbitron, and Share Tech Mono are rendering correctly.
