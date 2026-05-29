package dev.stranik.musicapp.presentation.viewmodel

import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.stranik.musicapp.domain.Creator
import dev.stranik.musicapp.domain.model.Album
import dev.stranik.musicapp.domain.model.Artist
import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.domain.usecase.GetFeaturedAlbumsUseCase
import dev.stranik.musicapp.domain.usecase.GetRecentlyPlayedUseCase
import dev.stranik.musicapp.domain.usecase.GetPopularArtistsUseCase
import dev.stranik.musicapp.domain.usecase.GetNewReleasesUseCase
import dev.stranik.musicapp.presentation.mapper.HomeUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val featuredAlbums: List<Album> = emptyList(),
    val recentlyPlayed: List<Track> = emptyList(),
    val popularArtists: List<Artist> = emptyList(),
    val newReleases: List<Album> = emptyList(),
    val error: String? = null
)

class HomeViewModel(
    private val getFeaturedAlbumsUseCase: GetFeaturedAlbumsUseCase,
    private val getRecentlyPlayedUseCase: GetRecentlyPlayedUseCase,
    private val getPopularArtistsUseCase: GetPopularArtistsUseCase,
    private val getNewReleasesUseCase: GetNewReleasesUseCase,
    private val homeUiMapper: HomeUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeContent()
    }

    fun loadHomeContent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                combine(
                    getFeaturedAlbumsUseCase(),
                    getRecentlyPlayedUseCase(),
                    getPopularArtistsUseCase(),
                    getNewReleasesUseCase()
                ) { featured, recent, artists, releases ->
                    HomeUiState(
                        isLoading = false,
                        featuredAlbums = featured.map(homeUiMapper::toAlbum),
                        recentlyPlayed = recent.map(homeUiMapper::toTrack),
                        popularArtists = artists.map(homeUiMapper::toArtist),
                        newReleases = releases.map(homeUiMapper::toAlbum)
                    )
                }.collect { state -> _uiState.value = state }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(error = null) }
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val getFeaturedAlbums = Creator.provideGetFeaturedAlbums()
                val getRecentlyPlayed = Creator.provideGetRecentlyPlayed()
                val getPopularArtists = Creator.provideGetPopularArtists()
                val getNewReleases = Creator.provideGetNewReleases()

                HomeViewModel(
                    getFeaturedAlbumsUseCase = getFeaturedAlbums,
                    getRecentlyPlayedUseCase = getRecentlyPlayed,
                    getPopularArtistsUseCase = getPopularArtists,
                    getNewReleasesUseCase = getNewReleases,
                    homeUiMapper = HomeUiMapper()
                )
            }
        }
    }
}
