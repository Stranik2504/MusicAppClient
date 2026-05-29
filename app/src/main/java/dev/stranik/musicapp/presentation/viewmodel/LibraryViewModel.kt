package dev.stranik.musicapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.stranik.musicapp.domain.Creator
import dev.stranik.musicapp.domain.model.Playlist
import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.domain.usecase.CreatePlaylistUseCase
import dev.stranik.musicapp.domain.usecase.GetLikedTracksUseCase
import dev.stranik.musicapp.domain.usecase.GetUserPlaylistsUseCase
import dev.stranik.musicapp.presentation.mapper.HomeUiMapper
import dev.stranik.musicapp.presentation.mapper.LibraryUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
                combine(
                    getUserPlaylistsUseCase(),
                    getLikedTracksUseCase()
                ) { playlists, liked ->
                    LibraryUiState(
                        isLoading = false,
                        playlists = playlists.map(libraryUiMapper::toPlaylist),
                        likedTracks = liked.map(libraryUiMapper::toTrack)
                    )
                }.collect { state -> _uiState.value = state }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onCreatePlaylist(title: String) {
        viewModelScope.launch { createPlaylistUseCase(title = title) }
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val getUserPlaylists = Creator.provideGetUserPlaylists()
                val getLikedTracks = Creator.provideGetLikedTracks()
                val createPlaylist = Creator.provideCreatePlaylist()

                LibraryViewModel(
                    getUserPlaylistsUseCase = getUserPlaylists,
                    getLikedTracksUseCase = getLikedTracks,
                    createPlaylistUseCase = createPlaylist,
                    libraryUiMapper = LibraryUiMapper()
                )
            }
        }
    }
}