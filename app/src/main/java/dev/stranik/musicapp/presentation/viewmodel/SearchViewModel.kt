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
import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.domain.usecase.SearchUseCase
import dev.stranik.musicapp.presentation.mapper.LibraryUiMapper
import dev.stranik.musicapp.presentation.mapper.SearchUiMapper
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

sealed class SearchUiState {
    object Idle    : SearchUiState()
    object Loading : SearchUiState()
    data class Success(
        val tracks:  List<Track>  = emptyList(),
        val artists: List<Artist> = emptyList(),
        val albums:  List<Album>  = emptyList()
    ) : SearchUiState()
    data class Empty(val query: String) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val searchUseCase: SearchUseCase,
    private val searchUiMapper: SearchUiMapper
) : ViewModel() {

    private val _query    = MutableStateFlow("")
    private val _uiState  = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
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
            val tracks  = result.tracks.map(searchUiMapper::toTrack)
            val artists = result.artists.map(searchUiMapper::toArtist)
            val albums  = result.albums.map(searchUiMapper::toAlbum)

            _uiState.value = if (tracks.isEmpty() && artists.isEmpty() && albums.isEmpty()) {
                SearchUiState.Empty(query)
            } else {
                SearchUiState.Success(tracks, artists, albums)
            }
        } catch (e: Exception) {
            _uiState.value = SearchUiState.Error(e.message ?: "Неизвестная ошибка")
        }
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val search = Creator.provideSearch()

                SearchViewModel(
                    searchUseCase = search,
                    searchUiMapper = SearchUiMapper()
                )
            }
        }
    }
}
