package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.repository.PlayerRepository
import dev.stranik.musicapp.presentation.mapper.PlayerState
import kotlinx.coroutines.flow.Flow

class ObservePlayerStateUseCase(
    private val playerRepository: PlayerRepository
) {
    operator fun invoke(): Flow<PlayerState> = playerRepository.playerState
}
