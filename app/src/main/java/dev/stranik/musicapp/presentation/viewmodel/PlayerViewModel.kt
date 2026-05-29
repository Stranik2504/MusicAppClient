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
import dev.stranik.musicapp.presentation.mapper.PlayerUiMapper
import dev.stranik.musicapp.domain.usecase.PauseTrackUseCase
import dev.stranik.musicapp.domain.usecase.SkipNextUseCase
import dev.stranik.musicapp.domain.usecase.SkipPreviousUseCase
import dev.stranik.musicapp.domain.usecase.LikeTrackUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    private val playerStateUseCase: ObservePlayerStateUseCase,
    private val playerUiMapper: PlayerUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        observePlayerState()
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            playerStateUseCase().collect { playerState ->
                _uiState.value = playerUiMapper.toUiState(playerState)
            }
        }
    }

    fun play(track: Track) {
        viewModelScope.launch { playTrackUseCase(trackId = track.id) }
    }

    fun onPlayPause() {
        viewModelScope.launch {
            if (_uiState.value.isPlaying) pauseTrackUseCase()
            else playTrackUseCase(trackId = _uiState.value.currentTrack?.id ?: return@launch)
        }
    }

    fun onSkipNext() = viewModelScope.launch { skipNextUseCase() }
    fun onSkipPrevious() = viewModelScope.launch { skipPreviousUseCase() }

    fun onSeek(fraction: Float) {
        val durationMs = _uiState.value.durationMs
        viewModelScope.launch { seekUseCase(positionMs = (durationMs * fraction).toLong()) }
    }

    fun onLikeToggle() {
        val trackId = _uiState.value.currentTrack?.id ?: return
        viewModelScope.launch {
            likeTrackUseCase(trackId = trackId)
            _uiState.update { it.copy(isLiked = !it.isLiked) }
        }
    }

    fun onShuffleToggle() {
        _uiState.update { it.copy(isShuffleEnabled = !it.isShuffleEnabled) }
    }

    fun onRepeatToggle() {
        _uiState.update {
            it.copy(
                repeatMode = when (it.repeatMode) {
                    RepeatMode.OFF -> RepeatMode.ALL
                    RepeatMode.ALL -> RepeatMode.ONE
                    RepeatMode.ONE -> RepeatMode.OFF
                }
            )
        }
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val playTrack = Creator.providePlayTrack()
                val pauseTrack = Creator.providePauseTrack()
                val skipNext = Creator.provideSkipNext()
                val skipPrevious = Creator.provideSkipPrevious()
                val seek = Creator.provideSeek()
                val likeTrack = Creator.provideLikeTrack()
                val observePlayerState = Creator.provideObservePlayerState()

                PlayerViewModel(
                    playTrackUseCase = playTrack,
                    pauseTrackUseCase = pauseTrack,
                    skipNextUseCase = skipNext,
                    skipPreviousUseCase = skipPrevious,
                    seekUseCase = seek,
                    likeTrackUseCase = likeTrack,
                    playerStateUseCase = observePlayerState,
                    playerUiMapper = PlayerUiMapper(),
                )
            }
        }
    }
}