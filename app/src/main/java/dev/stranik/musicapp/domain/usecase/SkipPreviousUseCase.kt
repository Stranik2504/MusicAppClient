package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.repository.PlayerRepository

class SkipPreviousUseCase(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke() {
        playerRepository.skipPrevious()
    }
}
