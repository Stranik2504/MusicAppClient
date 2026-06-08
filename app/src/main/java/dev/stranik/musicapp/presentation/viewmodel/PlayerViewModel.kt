package dev.stranik.musicapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.stranik.musicapp.domain.Creator
import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.domain.usecase.ObservePlayerStateUseCase
import dev.stranik.musicapp.domain.usecase.PlayTrackUseCase
import dev.stranik.musicapp.domain.usecase.SeekUseCase
import dev.stranik.musicapp.domain.usecase.PauseTrackUseCase
import dev.stranik.musicapp.domain.usecase.SkipNextUseCase
import dev.stranik.musicapp.domain.usecase.SkipPreviousUseCase
import dev.stranik.musicapp.domain.usecase.LikeTrackUseCase
import dev.stranik.musicapp.domain.usecase.UnlikeTrackUseCase
import dev.stranik.musicapp.domain.usecase.SetRepeatModeUseCase
import dev.stranik.musicapp.domain.usecase.SetShuffleUseCase
import dev.stranik.musicapp.domain.usecase.UpdatePlayerLikeStatusUseCase
import dev.stranik.musicapp.presentation.mapper.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class RepeatMode { OFF, ALL, ONE }

data class PlayerUiState(
    val currentTrack: Track? = null,
    val isPlaying: Boolean = false,
    val progressMs: Long = 0L,
    val durationMs: Long = 0L,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val isLiked: Boolean = false,
    val queue: List<Track> = emptyList()
) {
    val progressFraction: Float
        get() = if (durationMs > 0) progressMs.toFloat() / durationMs else 0f

    val currentTimeFormatted: String
        get() = formatTime(progressMs)

    val totalTimeFormatted: String
        get() = formatTime(durationMs)

    private fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }
}


class PlayerViewModel(
    private val playTrackUseCase: PlayTrackUseCase,
    private val pauseTrackUseCase: PauseTrackUseCase,
    private val skipNextUseCase: SkipNextUseCase,
    private val skipPreviousUseCase: SkipPreviousUseCase,
    private val seekUseCase: SeekUseCase,
    private val likeTrackUseCase: LikeTrackUseCase,
    private val unlikeTrackUseCase: UnlikeTrackUseCase,
    private val setRepeatModeUseCase: SetRepeatModeUseCase,
    private val setShuffleUseCase: SetShuffleUseCase,
    private val updatePlayerLikeStatusUseCase: UpdatePlayerLikeStatusUseCase,
    private val playerStateUseCase: ObservePlayerStateUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        observePlayerState()
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            playerStateUseCase().collect { playerState ->
                _uiState.value = playerState.toUiState()
            }
        }
    }

    fun play(trackId: String) {
        viewModelScope.launch {
            playTrackUseCase(trackId = trackId)
        }
    }

    fun onPlayPause() {
        viewModelScope.launch {
            if (_uiState.value.isPlaying) {
                pauseTrackUseCase()
            } else {
                val trackId = _uiState.value.currentTrack?.id
                if (trackId != null) {
                    playTrackUseCase.resume()
                }
            }
        }
    }

    fun onSkipNext() = viewModelScope.launch { skipNextUseCase() }
    fun onSkipPrevious() = viewModelScope.launch { skipPreviousUseCase() }

    fun onSeek(fraction: Float) {
        val durationMs = _uiState.value.durationMs

        viewModelScope.launch {
            seekUseCase(positionMs = (durationMs * fraction).toLong())
        }
    }

    fun onLikeToggle() {
        val track = _uiState.value.currentTrack ?: return
        val trackId = track.id.toLongOrNull() ?: return
        val isCurrentlyLiked = _uiState.value.isLiked

        viewModelScope.launch {
            val result = if (isCurrentlyLiked) {
                unlikeTrackUseCase(trackId)
            } else {
                likeTrackUseCase(trackId)
            }
            
            if (result.isSuccess) {
                // Синхронизируем состояние лайка с репозиторием плеера
                updatePlayerLikeStatusUseCase(!isCurrentlyLiked)
            }
        }
    }

    fun onShuffleToggle() {
        val newValue = !_uiState.value.isShuffleEnabled
        viewModelScope.launch {
            setShuffleUseCase(newValue)
        }
    }

    fun onRepeatToggle() {
        val nextMode = when (_uiState.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        viewModelScope.launch {
            setRepeatModeUseCase(nextMode)
        }
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val playerRepo = Creator.providePlayerRepository(context)
                val trackRepo = Creator.provideTrackRepository(context)
                
                val playTrack = Creator.providePlayTrack(playerRepo, trackRepo)
                val pauseTrack = Creator.providePauseTrack(playerRepo)
                val skipNext = Creator.provideSkipNext(playerRepo)
                val skipPrevious = Creator.provideSkipPrevious(playerRepo)
                val seek = Creator.provideSeek(playerRepo)
                val likeTrack = Creator.provideLikeTrack(trackRepo)
                val unlikeTrack = Creator.provideUnlikeTrack(trackRepo)
                val setRepeatMode = Creator.provideSetRepeatMode(playerRepo)
                val setShuffle = Creator.provideSetShuffle(playerRepo)
                val updateLikeStatus = Creator.provideUpdatePlayerLikeStatus(playerRepo)
                val observePlayerState = Creator.provideObservePlayerState(playerRepo)

                PlayerViewModel(
                    playTrackUseCase = playTrack,
                    pauseTrackUseCase = pauseTrack,
                    skipNextUseCase = skipNext,
                    skipPreviousUseCase = skipPrevious,
                    seekUseCase = seek,
                    likeTrackUseCase = likeTrack,
                    unlikeTrackUseCase = unlikeTrack,
                    setRepeatModeUseCase = setRepeatMode,
                    setShuffleUseCase = setShuffle,
                    updatePlayerLikeStatusUseCase = updateLikeStatus,
                    playerStateUseCase = observePlayerState,
                )
            }
        }
    }
}
