package dev.stranik.musicapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.stranik.musicapp.domain.model.Playlist
import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.domain.usecase.AddTrackToPlaylistUseCase
import dev.stranik.musicapp.domain.usecase.GetAlbumUseCase
import dev.stranik.musicapp.domain.usecase.GetPlaylistUseCase
import dev.stranik.musicapp.domain.usecase.GetTrackUseCase
import dev.stranik.musicapp.domain.usecase.GetUserPlaylistsUseCase
import dev.stranik.musicapp.domain.usecase.LikeTrackUseCase
import dev.stranik.musicapp.domain.usecase.PlayPlaylistUseCase
import dev.stranik.musicapp.domain.usecase.UnlikeTrackUseCase
import dev.stranik.musicapp.presentation.mapper.toPlaylist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlaylistDetailUiState(
    val isPlaylist: Boolean = true,
    val playlist: Playlist? = null,
    val playlists: List<Playlist> = emptyList(),
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class PlaylistDetailViewModel(
    private val playlistId: String,
    private val isPlaylist: Boolean = true,
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val playPlaylistUseCase: PlayPlaylistUseCase,
    private val getAlbumUseCase: GetAlbumUseCase,
    private val getTrackUseCase: GetTrackUseCase,
    private val getUserPlaylistsUseCase: GetUserPlaylistsUseCase,
    private val unlikeTrackUseCase: UnlikeTrackUseCase,
    private val likeTrackUseCase: LikeTrackUseCase,
    private val addTrackToPlaylistUseCase: AddTrackToPlaylistUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistDetailUiState(isLoading = true))
    val uiState: StateFlow<PlaylistDetailUiState> = _uiState.asStateFlow()

    init {
        if (isPlaylist)
            loadPlaylist()
        else
            loadAlbum()
    }

    fun loadPlaylist() {
        viewModelScope.launch {
            _uiState.update { it.copy(isPlaylist = true, isLoading = true, error = null) }
            
            getPlaylistUseCase(playlistId)
                .onSuccess { playlist ->
                    _uiState.update { it.copy(playlist = playlist) }
                    loadTracks(playlist.trackIds)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = "Не удалось загрузить плейлист") }
                }

            getUserPlaylistsUseCase()
                .onSuccess { playlists ->
                    _uiState.update { it.copy(playlists = playlists) }
                }
        }
    }

    fun loadAlbum() {
        viewModelScope.launch {
            _uiState.update { it.copy(isPlaylist = false, isLoading = true, error = null) }

            getAlbumUseCase(playlistId.toLong())
                .onSuccess { album ->
                    _uiState.update { it.copy(playlist = album.toPlaylist()) }
                    loadTracks(album.tracks.map { it.toString() })
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = "Не удалось загрузить альбом") }
                }

            getUserPlaylistsUseCase()
            .onSuccess { playlists ->
                _uiState.update { it.copy(playlists = playlists) }
            }
        }
    }

    private suspend fun loadTracks(trackIds: List<String>) {
        val tracks = mutableListOf<Track>()
        var hasError = false

        trackIds.forEach { id ->
            getTrackUseCase(id.toLong())
                .onSuccess { if (!tracks.any { it.id != id }) tracks.add(it) }
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
            playPlaylistUseCase(
                tracks = uiState.value.tracks,
                initialTrack = track
            )
        }
    }

    fun toggleLike(track: Track) {
        viewModelScope.launch {
            val result = if (track.isLiked) {
                unlikeTrackUseCase(track.id.toLong())
            } else {
                likeTrackUseCase(track.id.toLong())
            }

            if (result.isSuccess) {
                if (isPlaylist)
                    loadPlaylist()
                else
                    loadAlbum()
            }
        }
    }

    fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            val result = addTrackToPlaylistUseCase(playlist.id, track.id)

            if (result.isSuccess) {
                if (isPlaylist)
                    loadPlaylist()
                else
                    loadAlbum()
            } else {
                _uiState.update { it.copy(error = result.exceptionOrNull()?.message) }
            }
        }
    }

    companion object {
        fun getViewModelFactory(
            playlistId: String,
            isPlaylist: Boolean = true,
            getPlaylistUseCase: GetPlaylistUseCase,
            playPlaylistUseCase: PlayPlaylistUseCase,
            getAlbumUseCase: GetAlbumUseCase,
            getTrackUseCase: GetTrackUseCase,
            getUserPlaylistsUseCase: GetUserPlaylistsUseCase,
            unlikeTrackUseCase: UnlikeTrackUseCase,
            likeTrackUseCase: LikeTrackUseCase,
            addTrackToPlaylistUseCase: AddTrackToPlaylistUseCase
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlaylistDetailViewModel(
                    playlistId = playlistId,
                    isPlaylist = isPlaylist,
                    getPlaylistUseCase = getPlaylistUseCase,
                    playPlaylistUseCase = playPlaylistUseCase,
                    getAlbumUseCase = getAlbumUseCase,
                    getTrackUseCase = getTrackUseCase,
                    getUserPlaylistsUseCase = getUserPlaylistsUseCase,
                    unlikeTrackUseCase = unlikeTrackUseCase,
                    likeTrackUseCase = likeTrackUseCase,
                    addTrackToPlaylistUseCase = addTrackToPlaylistUseCase
                )
            }
        }
    }
}
