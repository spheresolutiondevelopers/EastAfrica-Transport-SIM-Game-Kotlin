package com.transportsim.domain.usecases

import com.transportsim.domain.models.Vehicle
import com.transportsim.domain.repositories.PlayerRepository
import kotlinx.coroutines.flow.Flow

class GetPlayerVehiclesUseCase(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(): List<Vehicle> =
        playerRepository.getPlayerVehicles()

    fun observe(): Flow<List<Vehicle>> =
        playerRepository.observeVehicles()
}