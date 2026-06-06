package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.repository.PlayerRepository

class SetShuffleUseCase(private val playerRepository: PlayerRepository) {
    suspend operator fun invoke(enabled: Boolean) {
        playerRepository.setShuffleEnabled(enabled)
    }
}
