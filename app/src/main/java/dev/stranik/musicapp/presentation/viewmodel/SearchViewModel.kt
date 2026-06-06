package dev.stranik.musicapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.stranik.musicapp.domain.Creator
import dev.stranik.musicapp.domain.model.Album
import dev.stranik.musicapp.domain.model.Artist
import dev.stranik.musicapp.domain.model.Playlist
import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.domain.usecase.AddTrackToPlaylistUseCase
import dev.stranik.musicapp.domain.usecase.GetUserPlaylistsUseCase
import dev.stranik.musicapp.domain.usecase.LikeTrackUseCase
import dev.stranik.musicapp.domain.usecase.SearchUseCase
import dev.stranik.musicapp.domain.usecase.UnlikeTrackUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(
        val playlists: List<Playlist> = emptyList(),
        val tracks: List<Track> = emptyList(),
        val artists: List<Artist> = emptyList(),
        val albums: List<Album> = emptyList()
    ) : SearchUiState()

    data class Empty(val query: String) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val searchUseCase: SearchUseCase,
    private val getUserPlaylistsUseCase: GetUserPlaylistsUseCase,
    private val unlikeTrackUseCase: UnlikeTrackUseCase,
    private val likeTrackUseCase: LikeTrackUseCase,
    private val addTrackToPlaylistUseCase: AddTrackToPlaylistUseCase
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    val query: StateFlow<String> = _query.asStateFlow()

    init {
        _query
            .debounce(400L)
            .distinctUntilChanged()
            .onEach { q ->
                if (q.isBlank()) {
                    _uiState.value = SearchUiState.Idle
                } else {
                    search(q)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(query: String) {
        _query.value = query
    }

    fun onClearQuery() {
        _query.value = ""
        _uiState.value = SearchUiState.Idle
    }

    private suspend fun search(query: String) {
        _uiState.value = SearchUiState.Loading

        try {
            val result = searchUseCase(query)
            val playlists = getUserPlaylistsUseCase()

            if (playlists.isFailure) {
                _uiState.value = SearchUiState.Error(playlists.exceptionOrNull()?.message ?: "Неизвестная ошибка")
                return
            }

            _uiState.value = if (result.tracks.isEmpty() && result.artists.isEmpty() && result.albums.isEmpty()) {
                SearchUiState.Empty(query)
            } else {
                SearchUiState.Success(playlists.getOrThrow(), result.tracks, result.artists, result.albums)
            }
        } catch (e: Exception) {
            _uiState.value = SearchUiState.Error(e.message ?: "Неизвестная ошибка")
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
                search(_query.value)
            }
        }
    }

    fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            val result = addTrackToPlaylistUseCase(playlist.id, track.id)

            if (result.isSuccess) {
                search(_query.value)
            } else {
                _uiState.value = SearchUiState.Error(result.exceptionOrNull()?.message ?: "Неизвестная ошибка")
            }
        }
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val searchRepository = Creator.provideSearchRepository()
                val trackRepository = Creator.provideTrackRepository()
                val libraryRepository = Creator.provideLibraryRepository()

                val search = Creator.provideSearch(searchRepository)
                val getUserPlaylistsUseCase = Creator.provideGetUserPlaylists(libraryRepository)
                val unlikeTrackUseCase = Creator.provideUnlikeTrack(trackRepository)
                val likeTrackUseCase = Creator.provideLikeTrack(trackRepository)
                val addTrackToPlaylistUseCase = Creator.provideAddTrackToPlaylist(libraryRepository)

                SearchViewModel(
                    searchUseCase = search,
                    getUserPlaylistsUseCase = getUserPlaylistsUseCase,
                    unlikeTrackUseCase = unlikeTrackUseCase,
                    likeTrackUseCase = likeTrackUseCase,
                    addTrackToPlaylistUseCase = addTrackToPlaylistUseCase
                )
            }
        }
    }
}
