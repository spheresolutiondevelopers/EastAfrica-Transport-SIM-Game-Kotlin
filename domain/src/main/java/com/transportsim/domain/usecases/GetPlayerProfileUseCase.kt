package com.transportsim.domain.usecases

import com.transportsim.domain.models.PlayerProfile
import com.transportsim.domain.repositories.PlayerRepository
import kotlinx.coroutines.flow.Flow

class GetPlayerProfileUseCase(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(): PlayerProfile =
        playerRepository.getProfile()

    fun observe(): Flow<PlayerProfile> =
        playerRepository.observeProfile()
}