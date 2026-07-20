# Implementation Plan - Fix Theme and Typography Build Errors

The project currently fails to build due to unresolved references in `Theme.kt` and `Type.kt`. This plan addresses these by aligning color naming and providing missing resource definitions.

## Proposed Changes

### [Component] UI Theme

#### [MODIFY] [Theme.kt](file:///C:/Users/lenovo/Projects/Transport%20simulation/TransportsimAndroid/app/src/main/java/com/transportsim/app/ui/theme/Theme.kt)
Update the color scheme definitions to use the actual color constants defined in `Color.kt`.
- `DarkBackground` -> `BgDeep`
- `DarkSurface` -> `BgPanel`
- `DarkCard` -> `BgCard`
- `TextPrimary` -> `TextPri`
- `DarkBorder` -> `Border`
- `LightBackground` -> `LightBgDeep`
- `LightSurface` -> `LightBgPanel`
- `LightCard` -> `LightBgCard`

#### [NEW] [font_certs.xml](file:///C:/Users/lenovo/Projects/Transport%20simulation/TransportsimAndroid/app/src/main/res/values/font_certs.xml)
Create a new resource file to define `com_google_android_gms_fonts_certs`, which is required for using Google Fonts in Compose.

## Verification Plan

### Automated Tests
- Run `gradle_build` (specifically `app:assembleDebug`) to ensure the compilation errors are resolved.

### Manual Verification
- Render Compose previews for components using the theme (if any are found) to ensure colors look as expected.
