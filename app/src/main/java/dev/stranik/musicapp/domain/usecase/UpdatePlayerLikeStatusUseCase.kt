package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.repository.PlayerRepository

class UpdatePlayerLikeStatusUseCase(private val playerRepository: PlayerRepository) {
    suspend operator fun invoke(isLiked: Boolean) {
        playerRepository.updateLikeStatus(isLiked)
    }
}
