package dev.stranik.musicapp.domain.repository

import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.presentation.mapper.PlayerState
import dev.stranik.musicapp.presentation.viewmodel.RepeatMode
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    val playerState: Flow<PlayerState>
    suspend fun playTrack(track: Track, hlsUrl: String)
    suspend fun pause()
    suspend fun resume()
    suspend fun seekTo(positionMs: Long)
    suspend fun skipNext()
    suspend fun skipPrevious()
    suspend fun setRepeatMode(repeatMode: RepeatMode)
    suspend fun setShuffleEnabled(enabled: Boolean)
    suspend fun updateLikeStatus(isLiked: Boolean)
}
