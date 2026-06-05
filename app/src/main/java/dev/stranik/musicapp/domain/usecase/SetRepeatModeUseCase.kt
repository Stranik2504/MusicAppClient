package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.repository.PlayerRepository
import dev.stranik.musicapp.presentation.viewmodel.RepeatMode

class SetRepeatModeUseCase(private val playerRepository: PlayerRepository) {
    suspend operator fun invoke(repeatMode: RepeatMode) {
        playerRepository.setRepeatMode(repeatMode)
    }
}
