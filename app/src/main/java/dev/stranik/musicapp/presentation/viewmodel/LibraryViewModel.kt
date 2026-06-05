package dev.stranik.musicapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.async
import dev.stranik.musicapp.domain.Creator
import dev.stranik.musicapp.domain.model.Playlist
import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.domain.usecase.AddTrackToPlaylistUseCase
import dev.stranik.musicapp.domain.usecase.CreatePlaylistUseCase
import dev.stranik.musicapp.domain.usecase.GetLikedTracksUseCase
import dev.stranik.musicapp.domain.usecase.GetUserPlaylistsUseCase
import dev.stranik.musicapp.domain.usecase.LikeTrackUseCase
import dev.stranik.musicapp.domain.usecase.UnlikeTrackUseCase
import dev.stranik.musicapp.presentation.mapper.LibraryUiMapper
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LibraryUiState(
    val isLoading: Boolean = false,
    val playlists: List<Playlist> = emptyList(),
    val likedTracks: List<Track> = emptyList(),
    val error: String? = null
)

enum class LibraryTab(val label: String) {
    PLAYLISTS("Плейлисты"),
    LIKED("Любимые треки")
}

class LibraryViewModel(
    private val getUserPlaylistsUseCase: GetUserPlaylistsUseCase,
    private val getLikedTracksUseCase: GetLikedTracksUseCase,
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val likeTrackUseCase: LikeTrackUseCase,
    private val unlikeTrackUseCase: UnlikeTrackUseCase,
    private val addTrackToPlaylistUseCase: AddTrackToPlaylistUseCase,
    private val libraryUiMapper: LibraryUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        loadLibrary()
    }

    fun loadLibrary() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                coroutineScope {
                    val playlistsDeferred = async { getUserPlaylistsUseCase() }
                    val likedTracksDeferred = async { getLikedTracksUseCase() }

                    val playlistsResult = playlistsDeferred.await()
                    val likedTracksResult = likedTracksDeferred.await()

                    if (playlistsResult.isSuccess && likedTracksResult.isSuccess) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                playlists = playlistsResult.getOrThrow().map(libraryUiMapper::toPlaylist),
                                likedTracks = likedTracksResult.getOrThrow().map(libraryUiMapper::toTrack)
                            )
                        }
                    } else {
                        val errorMsg = playlistsResult.exceptionOrNull()?.message
                            ?: likedTracksResult.exceptionOrNull()?.message
                            ?: "Ошибка загрузки данных"
                        _uiState.update { it.copy(isLoading = false, error = errorMsg) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onCreatePlaylist(title: String, description: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = createPlaylistUseCase(title, description)

            if (result.isSuccess) {
                loadLibrary()
            } else {
                _uiState.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
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
                loadLibrary()
            }
        }
    }

    fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            val result = addTrackToPlaylistUseCase(playlist.id, track.id)
            if (result.isSuccess) {
                loadLibrary()
            } else {
                _uiState.update { it.copy(error = result.exceptionOrNull()?.message) }
            }
        }
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val libraryRepo = Creator.provideLibraryRepository()
                val trackRepo = Creator.provideTrackRepository()
                val getUserPlaylists = Creator.provideGetUserPlaylists(libraryRepo)
                val getLikedTracks = Creator.provideGetLikedTracks(libraryRepo)
                val createPlaylist = Creator.provideCreatePlaylist(libraryRepo)
                val likeTrack = Creator.provideLikeTrack(trackRepo)
                val unlikeTrack = Creator.provideUnlikeTrack(trackRepo)
                val addTrackToPlaylist = Creator.provideAddTrackToPlaylist(libraryRepo)

                LibraryViewModel(
                    getUserPlaylistsUseCase = getUserPlaylists,
                    getLikedTracksUseCase = getLikedTracks,
                    createPlaylistUseCase = createPlaylist,
                    likeTrackUseCase = likeTrack,
                    unlikeTrackUseCase = unlikeTrack,
                    addTrackToPlaylistUseCase = addTrackToPlaylist,
                    libraryUiMapper = LibraryUiMapper()
                )
            }
        }
    }
}
