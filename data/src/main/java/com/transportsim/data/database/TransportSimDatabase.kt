package com.transportsim.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.transportsim.data.database.dao.*
import com.transportsim.data.database.entities.*
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

@Database(
    entities = [
        PlayerProfileEntity::class,
        PlayerVehicleEntity::class,
        VehicleUpgradeEntity::class,
        JourneySessionEntity::class,
        StopEventEntity::class,
        ActiveMissionEntity::class,
        EconomyLedgerEntity::class,
        DailyStatsEntity::class,
        AchievementEntity::class,
        PartsInventoryEntity::class,
        RouteUnlockStateEntity::class,
        RouteWaypointEntity::class,
        TerrainChunkMetadataEntity::class,
        RouteStatsEntity::class,
        TrainingProgressEntity::class
    ],
    version = 4, // Increment this when schema changes, and add a migration
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class TransportSimDatabase : RoomDatabase() {

    abstract fun playerProfileDao(): PlayerProfileDao
    abstract fun playerVehicleDao(): PlayerVehicleDao
    abstract fun vehicleUpgradeDao(): VehicleUpgradeDao
    abstract fun journeySessionDao(): JourneySessionDao
    abstract fun stopEventDao(): StopEventDao
    abstract fun activeMissionDao(): ActiveMissionDao
    abstract fun economyLedgerDao(): EconomyLedgerDao
    abstract fun dailyStatsDao(): DailyStatsDao
    abstract fun achievementDao(): AchievementDao
    abstract fun partsInventoryDao(): PartsInventoryDao
    abstract fun routeUnlockStateDao(): RouteUnlockStateDao
    abstract fun routeWaypointDao(): RouteWaypointDao
    abstract fun terrainChunkMetadataDao(): TerrainChunkMetadataDao
    abstract fun routeStatsDao(): RouteStatsDao
    abstract fun trainingProgressDao(): TrainingProgressDao

    companion object {
        @Volatile
        private var INSTANCE: TransportSimDatabase? = null

        private const val DB_NAME = "transportsim.db"
        private const val PASSPHRASE = "your_secure_passphrase_here" // In production, derive from user pin or biometrics

        fun getInstance(context: Context): TransportSimDatabase {
            System.loadLibrary("sqlcipher")
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TransportSimDatabase::class.java,
                    DB_NAME
                )
                    .openHelperFactory(SupportOpenHelperFactory(PASSPHRASE.toByteArray()))
                    // Add migrations when version > 1
                    // .addMigrations(Migrations.MIGRATION_1_2, Migrations.MIGRATION_2_3)
                    .fallbackToDestructiveMigration() // For dev; remove for production after migrations are solid
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // For testing (no encryption)
        fun getTestInstance(context: Context): TransportSimDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                TransportSimDatabase::class.java,
                DB_NAME + "_test"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}