package com.transportsim.app.di

import com.transportsim.data.repository.*
import com.transportsim.domain.repositories.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPlayerRepository(impl: PlayerRepositoryImpl): PlayerRepository

    @Binds
    @Singleton
    abstract fun bindFleetRepository(impl: FleetRepositoryImpl): FleetRepository

    @Binds
    @Singleton
    abstract fun bindMissionRepository(impl: MissionRepositoryImpl): MissionRepository

    @Binds
    @Singleton
    abstract fun bindRouteRepository(impl: RouteRepositoryImpl): RouteRepository

    @Binds
    @Singleton
    abstract fun bindEconomyRepository(impl: EconomyRepositoryImpl): EconomyRepository

    @Binds
    @Singleton
    abstract fun bindCatalogRepository(impl: CatalogRepositoryImpl): CatalogRepository

    @Binds
    @Singleton
    abstract fun bindTerrainRepository(impl: TerrainRepositoryImpl): TerrainRepository

    @Binds
    @Singleton
    abstract fun bindTrainingRepository(impl: TrainingRepositoryImpl): TrainingRepository
}
