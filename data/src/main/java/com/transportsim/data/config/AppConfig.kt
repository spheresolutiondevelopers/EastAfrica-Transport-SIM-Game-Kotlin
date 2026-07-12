package com.transportsim.data.config

object AppConfig {
    const val DATABASE_NAME = "transportsim.db"
    const val DATABASE_VERSION = 1
    const val SHARED_PREFS_NAME = "transportsim_prefs"
    const val ENCRYPTED_PREFS_NAME = "transportsim_encrypted_prefs"

    // Vehicle constants
    const val DEFAULT_FUEL_CAPACITY_L = 200f
    const val FUEL_COST_PER_LITER_KSH = 180
    const val BASE_REPAIR_COST_KSH = 1500

    // Route constants
    const val CHUNK_SIZE_M = 128f
    const val ELEVATION_GRID_RESOLUTION = 65

    // Simulation constants
    const val PHYSICS_TICK_RATE_HZ = 1000
    const val AUTOSAVE_INTERVAL_SECONDS = 5
    const val PRELOAD_RADIUS_CHUNKS = 2

    // Mission constants
    const val DAILY_MISSION_COUNT = 6
    const val MISSION_EXPIRY_HOURS = 24
}