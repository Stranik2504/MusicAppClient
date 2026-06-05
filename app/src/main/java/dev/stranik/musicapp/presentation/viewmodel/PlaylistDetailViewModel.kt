package dev.stranik.musicapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.stranik.musicapp.domain.model.Playlist
import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.domain.repository.LibraryRepository
import dev.stranik.musicapp.domain.repository.TrackRepository
import dev.stranik.musicapp.domain.usecase.GetPlaylistUseCase
import dev.stranik.musicapp.domain.usecase.PlayPlaylistUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlaylistDetailUiState(
    val playlist: Playlist? = null,
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class PlaylistDetailViewModel(
    private val playlistId: String,
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val trackRepository: TrackRepository,
    private val playPlaylistUseCase: PlayPlaylistUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistDetailUiState(isLoading = true))
    val uiState: StateFlow<PlaylistDetailUiState> = _uiState.asStateFlow()

    init {
        loadPlaylist()
    }

    fun loadPlaylist() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            getPlaylistUseCase(playlistId)
                .onSuccess { playlist ->
                    _uiState.update { it.copy(playlist = playlist) }
                    loadTracks(playlist.trackIds)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = "Не удалось загрузить плейлист") }
                }
        }
    }

    private suspend fun loadTracks(trackIds: List<String>) {
        val tracks = mutableListOf<Track>()
        var hasError = false

        trackIds.forEach { id ->
            trackRepository.getTrack(id.toLong())
                .onSuccess { tracks.add(it) }
                .onFailure { hasError = true }
        }

        _uiState.update { 
            it.copy(
                tracks = tracks, 
                isLoading = false,
                error = if (hasError && tracks.isEmpty()) "Не удалось загрузить треки" else it.error
            )
        }
    }

    fun playTrack(track: Track) {
        viewModelScope.launch {
            // Используем новый UseCase для запуска плейлиста с выбранного трека
            playPlaylistUseCase(
                tracks = uiState.value.tracks,
                initialTrack = track
            )
        }
    }

    companion object {
        fun getViewModelFactory(
            playlistId: String,
            libraryRepository: LibraryRepository,
            trackRepository: TrackRepository,
            playPlaylistUseCase: PlayPlaylistUseCase
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlaylistDetailViewModel(
                    playlistId = playlistId,
                    getPlaylistUseCase = GetPlaylistUseCase(libraryRepository),
                    trackRepository = trackRepository,
                    playPlaylistUseCase = playPlaylistUseCase
                )
            }
        }
    }
}
