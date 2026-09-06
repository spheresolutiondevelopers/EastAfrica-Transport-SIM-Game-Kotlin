# Implementation Plan - Fleet Screen UI Refresh

Adjust the Fleet screen and its components to match the high-fidelity design specified in the `ui ux.html` (transportsim_v3_5-22.html) file, including fonts, colors, arrangement, and specific component styles.

## Proposed Changes

### [Theme & Colors]

#### [MODIFY] [Color.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/theme/Color.kt)
- Ensure all accent colors and background gradients from the CSS `:root` are available as Compose constants.

### [Fleet Screen Components]

#### [MODIFY] [FleetCategoryHeader.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/fleet/components/FleetCategoryHeader.kt)
- Update font to `Orbitron` for the category name.
- Apply uppercase and 2px letter spacing.
- Add a bottom border `1.dp` with `Border` color.
- Update the vehicle count text to use `Share Tech Mono` font.

#### [MODIFY] [FleetVehicleCard.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/fleet/components/FleetVehicleCard.kt)
- **Background**: Apply the specific linear gradient: `Brush.linearGradient(listOf(Color(0xFF0B1422), Color(0xFF050A12)))`.
- **Icon**: Create a 52x52 DP box with a category-specific gradient background (e.g., `fc-bus`, `fc-matatu`) and center the emoji/icon.
- **Level Badge**: Add an absolute-positioned badge in the top-right corner with `Gold` border and `Orbitron` text.
- **Stats Grid**: Refactor stats into a 2x2 grid. Each stat cell (`fc-stat`) should have a subtle background `Color(0xFFFFFFFF).copy(alpha = 0.04f)` and border.
- **Action Buttons**: Update the button styles to match `fc-btn` (border-only by default, specific colors for "Deploy").
- **Locked State**: Apply a grayscale filter and custom "Purchase" button layout when the vehicle is not owned.

### [Main Fleet Screen]

#### [MODIFY] [FleetScreen.kt](file:///C:/Users/lenovo/Projects/Transport simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/fleet/FleetScreen.kt)
- **Background**: Implement the "Mesh Background" effect (radial gradients) as a background layer for the screen.
- **Top Accent**: Add the 2.dp gradient top border (`Gold` to `Orange`).
- **Header**:
    - Style the title "Fleet Manager" with `Orbitron` and a horizontal gradient (`Gold` to `Orange`).
    - Improve the layout of the filter dropdown and "+ Purchase" button.
- **Layout**: Ensure the grid uses appropriate spacing (14.dp gap) and responsive column sizing.

## Verification Plan

### Automated Tests
- N/A (UI visual changes)

### Manual Verification
- Deploy the app and navigate to the Fleet screen.
- Compare the visual appearance with the HTML design:
    - Check the top border accent and screen title gradient.
    - Verify the mesh background is visible.
    - Check that category headers have the correct font and bottom line.
    - Verify the `FleetVehicleCard` layout: icon size, level badge, 2x2 stats grid, and action buttons.
    - Check the "Locked" state for unowned vehicles.
