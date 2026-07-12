package com.transportsim.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    // Example migration: add a new column to player_vehicles (e.g., "last_service_date")
    // This would be used if we increment the database version.
    // For now, version is 1, so we don't need any migrations yet.
    // But we'll keep a placeholder for future expansions.

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Example: add a new column
            // database.execSQL("ALTER TABLE player_vehicles ADD COLUMN last_service_date TEXT")
            // In reality, we'd add a proper migration when the schema changes.
        }
    }

    // We can add more migrations as needed.
    // val MIGRATION_2_3 = ...
}