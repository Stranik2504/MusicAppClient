package dev.stranik.musicapp.presentation.mapper

import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.presentation.viewmodel.PlayerUiState
import dev.stranik.musicapp.domain.model.Track as DomainTrack

class PlayerUiMapper {
    fun toUiState(playerState: PlayerState): PlayerUiState {
        // Простая трансформация: берем поля из PlayerState в PlayerUiState
        return PlayerUiState(
            currentTrack = playerState.currentTrack,
            isPlaying = playerState.isPlaying,
            progressMs = playerState.positionMs,
            durationMs = playerState.durationMs,
            isShuffleEnabled = playerState.isShuffleEnabled,
            repeatMode = playerState.repeatMode,
            isLiked = playerState.isLiked,
            queue = playerState.queue
        )
    }
}

// Для mapper-а нужен PlayerState - создадим простую модель в presentation или domain
data class PlayerState(
    val currentTrack: Track? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: dev.stranik.musicapp.presentation.viewmodel.RepeatMode = dev.stranik.musicapp.presentation.viewmodel.RepeatMode.OFF,
    val isLiked: Boolean = false,
    val queue: List<Track> = emptyList()
)

