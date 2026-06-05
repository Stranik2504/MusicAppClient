package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.repository.PlayerRepository

class SeekUseCase(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(positionMs: Long) {
        playerRepository.seekTo(positionMs)
    }
}
