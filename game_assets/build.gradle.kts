plugins {
    id("transportsim.assetpack")
}

android {
    namespace = "com.transportsim.game_assets"
    // No code; only assets
}

// This module is a Play Asset Delivery pack.
// It will be packaged as a separate asset pack in release builds.
// For development, the assets are still accessible via the `assets` folder.