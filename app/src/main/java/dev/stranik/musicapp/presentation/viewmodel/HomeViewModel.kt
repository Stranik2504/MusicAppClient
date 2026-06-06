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
import dev.stranik.musicapp.domain.usecase.GetFeaturedAlbumsUseCase
import dev.stranik.musicapp.domain.usecase.GetRecentlyPlayedUseCase
import dev.stranik.musicapp.domain.usecase.GetPopularArtistsUseCase
import dev.stranik.musicapp.domain.usecase.GetRecommendationTracksUseCase
import dev.stranik.musicapp.domain.usecase.GetUserPlaylistsUseCase
import dev.stranik.musicapp.domain.usecase.LikeTrackUseCase
import dev.stranik.musicapp.domain.usecase.UnlikeTrackUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val playlists: List<Playlist> = emptyList(),
    val featuredAlbums: List<Album> = emptyList(),
    val recentlyPlayed: List<Track> = emptyList(),
    val popularArtists: List<Artist> = emptyList(),
    val recommendationTracks: List<Track> = emptyList(),
    val error: String? = null
)

class HomeViewModel(
    private val getFeaturedAlbumsUseCase: GetFeaturedAlbumsUseCase,
    private val getRecentlyPlayedUseCase: GetRecentlyPlayedUseCase,
    private val getPopularArtistsUseCase: GetPopularArtistsUseCase,
    private val getRecommendationTracksUseCase: GetRecommendationTracksUseCase,
    private val getUserPlaylistsUseCase: GetUserPlaylistsUseCase,
    private val unlikeTrackUseCase: UnlikeTrackUseCase,
    private val likeTrackUseCase: LikeTrackUseCase,
    private val addTrackToPlaylistUseCase: AddTrackToPlaylistUseCase
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
                coroutineScope {
                    val featuredAlbumsDeferred = async { getFeaturedAlbumsUseCase() }
                    val recentlyPlayedDeferred = async { getRecentlyPlayedUseCase() }
                    val popularArtistsDeferred = async { getPopularArtistsUseCase() }
                    val recommendationTracksDeferred = async { getRecommendationTracksUseCase() }

                    val featuredAlbumsResult = featuredAlbumsDeferred.await()
                    val recentlyPlayedResult = recentlyPlayedDeferred.await()
                    val popularArtistsResult = popularArtistsDeferred.await()
                    val recommendationTracksResult = recommendationTracksDeferred.await()

                    if (featuredAlbumsResult.isSuccess &&
                        recentlyPlayedResult.isSuccess &&
                        popularArtistsResult.isSuccess &&
                        recommendationTracksResult.isSuccess
                    ) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                featuredAlbums = featuredAlbumsResult.getOrThrow(),
                                recentlyPlayed = recentlyPlayedResult.getOrThrow(),
                                popularArtists = popularArtistsResult.getOrThrow(),
                                recommendationTracks = recommendationTracksResult.getOrThrow()
                            )
                        }
                    } else {
                        val errorMsg = featuredAlbumsResult.exceptionOrNull()?.message
                            ?: recentlyPlayedResult.exceptionOrNull()?.message
                            ?: popularArtistsResult.exceptionOrNull()?.message
                            ?: recommendationTracksResult.exceptionOrNull()?.message
                            ?: "Ошибка загрузки данных"
                        _uiState.update { it.copy(isLoading = false, error = errorMsg) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }

            getUserPlaylistsUseCase()
            .onSuccess { playlists ->
                _uiState.update {
                    it.copy(
                        playlists = playlists,
                    )
                }
            };
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
                loadHomeContent()
            }
        }
    }

    fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            val result = addTrackToPlaylistUseCase(playlist.id, track.id)

            if (result.isSuccess) {
                loadHomeContent()
            } else {
                _uiState.update { it.copy(error = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(error = null) }
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val recommendationRepository = Creator.provideRecommendationRepository()
                val trackRepository = Creator.provideTrackRepository()
                val libraryRepository = Creator.provideLibraryRepository()

                val getFeaturedAlbums = Creator.provideGetFeaturedAlbums(recommendationRepository)
                val getRecentlyPlayed = Creator.provideGetRecentlyPlayed(recommendationRepository)
                val getPopularArtists = Creator.provideGetPopularArtists(recommendationRepository)
                val getNewReleases = Creator.provideGetNewReleases(recommendationRepository)
                val getUserPlaylistsUseCase = Creator.provideGetUserPlaylists(libraryRepository)
                val unlikeTrackUseCase = Creator.provideUnlikeTrack(trackRepository)
                val likeTrackUseCase = Creator.provideLikeTrack(trackRepository)
                val addTrackToPlaylistUseCase = Creator.provideAddTrackToPlaylist(libraryRepository)

                HomeViewModel(
                    getFeaturedAlbumsUseCase = getFeaturedAlbums,
                    getRecentlyPlayedUseCase = getRecentlyPlayed,
                    getPopularArtistsUseCase = getPopularArtists,
                    getRecommendationTracksUseCase = getNewReleases,
                    getUserPlaylistsUseCase = getUserPlaylistsUseCase,
                    unlikeTrackUseCase = unlikeTrackUseCase,
                    likeTrackUseCase = likeTrackUseCase,
                    addTrackToPlaylistUseCase = addTrackToPlaylistUseCase
                )
            }
        }
    }
}
