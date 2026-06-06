package dev.stranik.musicapp.presentation.mapper

import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.presentation.viewmodel.PlayerUiState
import dev.stranik.musicapp.domain.model.Track as DomainTrack

fun PlayerState.toUiState(): PlayerUiState {
    return PlayerUiState(
        currentTrack = currentTrack,
        isPlaying = isPlaying,
        progressMs = positionMs,
        durationMs = durationMs,
        isShuffleEnabled = isShuffleEnabled,
        repeatMode = repeatMode,
        isLiked = isLiked,
        queue = queue
    )
}

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

