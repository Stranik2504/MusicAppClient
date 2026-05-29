package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.presentation.mapper.PlayerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ObservePlayerStateUseCase {
    operator fun invoke(): Flow<PlayerState> = flow {
        // Фейковый генератор состояния плеера — эмитим несколько состояний с задержкой
        val sampleTrack = PlayerState(
            currentTrack = dev.stranik.musicapp.domain.model.Track(
                id = "p_t1",
                title = "Moonlight Serenade",
                artistName = "Luna Echo",
                albumTitle = "Midnight Dreams",
                coverUrl = "https://via.placeholder.com/300?text=Moonlight+Serenade",
                durationFormatted = "3:45",
                isLiked = false
            ),
            isPlaying = true,
            positionMs = 45000L,
            durationMs = 225000L,
            isShuffleEnabled = false,
            repeatMode = dev.stranik.musicapp.presentation.viewmodel.RepeatMode.OFF,
            isLiked = false,
            queue = emptyList()
        )

        emit(sampleTrack)
        delay(1500L)
        emit(sampleTrack.copy(positionMs = 60000L))
        delay(1500L)
        emit(sampleTrack.copy(isPlaying = false))
    }
}


