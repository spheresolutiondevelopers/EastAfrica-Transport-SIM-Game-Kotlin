package com.transportsim.app.di

import android.content.Context
import com.transportsim.data.database.TransportSimDatabase
import com.transportsim.data.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TransportSimDatabase {
        return TransportSimDatabase.getInstance(context)
    }

    @Provides
    fun providePlayerProfileDao(db: TransportSimDatabase): PlayerProfileDao = db.playerProfileDao()

    @Provides
    fun providePlayerVehicleDao(db: TransportSimDatabase): PlayerVehicleDao = db.playerVehicleDao()

    @Provides
    fun provideVehicleUpgradeDao(db: TransportSimDatabase): VehicleUpgradeDao = db.vehicleUpgradeDao()

    @Provides
    fun provideActiveMissionDao(db: TransportSimDatabase): ActiveMissionDao = db.activeMissionDao()

    @Provides
    fun provideEconomyLedgerDao(db: TransportSimDatabase): EconomyLedgerDao = db.economyLedgerDao()

    @Provides
    fun provideDailyStatsDao(db: TransportSimDatabase): DailyStatsDao = db.dailyStatsDao()

    @Provides
    fun provideAchievementDao(db: TransportSimDatabase): AchievementDao = db.achievementDao()

    @Provides
    fun provideRouteUnlockStateDao(db: TransportSimDatabase): RouteUnlockStateDao = db.routeUnlockStateDao()

    @Provides
    fun provideRouteWaypointDao(db: TransportSimDatabase): RouteWaypointDao = db.routeWaypointDao()

    @Provides
    fun provideRouteStatsDao(db: TransportSimDatabase): RouteStatsDao = db.routeStatsDao()

    @Provides
    fun provideTerrainChunkMetadataDao(db: TransportSimDatabase): TerrainChunkMetadataDao = db.terrainChunkMetadataDao()

    @Provides
    fun provideTrainingProgressDao(db: TransportSimDatabase): TrainingProgressDao = db.trainingProgressDao()
}
