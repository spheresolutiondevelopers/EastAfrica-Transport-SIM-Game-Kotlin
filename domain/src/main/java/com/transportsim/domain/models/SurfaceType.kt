package com.transportsim.domain.models

enum class SurfaceType(
    val displayName: String,
    val frictionDry: Float,
    val frictionWet: Float,
    val iriValue: Float  // International Roughness Index
) {
    TARMAC_NEW("New Tarmac", 0.85f, 0.55f, 0.8f),
    TARMAC_AGED("Aged Urban Tarmac", 0.80f, 0.48f, 2.5f),
    TARMAC_POTHOLED("Potholed Tarmac", 0.72f, 0.38f, 8.0f),
    DIRT_DRY("Dirt Road – Dry Laterite", 0.55f, 0.25f, 5.5f),
    DIRT_WET("Dirt Road – Wet Laterite", 0.28f, 0.18f, 9.0f),
    IRON_SHEET("Corrugated Iron Sheet", 0.35f, 0.18f, 7.0f),
    COBBLESTONE("Cobblestone", 0.70f, 0.40f, 4.5f),
    EXPRESSWAY("Expressway Flyover Deck", 0.87f, 0.60f, 0.5f),
    SAND("Sand / Unpaved Shoulder", 0.30f, 0.20f, 12.0f)
}