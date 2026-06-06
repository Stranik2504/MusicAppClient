package dev.stranik.musicapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.stranik.musicapp.domain.Creator
import dev.stranik.musicapp.domain.model.Artist
import dev.stranik.musicapp.domain.model.Playlist
import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.domain.usecase.AddTrackToPlaylistUseCase
import dev.stranik.musicapp.domain.usecase.GetArtistUseCase
import dev.stranik.musicapp.domain.usecase.GetTrackUseCase
import dev.stranik.musicapp.domain.usecase.GetUserPlaylistsUseCase
import dev.stranik.musicapp.domain.usecase.LikeTrackUseCase
import dev.stranik.musicapp.domain.usecase.UnlikeTrackUseCase
import dev.stranik.musicapp.presentation.ui.screen.ArtistDetailScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ArtistDetailUiState(
	val artist: Artist? = null,
	val topTracks: List<Track> = emptyList(),
	val playlists: List<Playlist> = emptyList(),
	val description: String = "Информация об артисте скоро будет расширена реальными данными.",
	val isLoading: Boolean = false,
	val error: String? = null
)

class ArtistDetailViewModel(
	private val artistId: String,
	private val getArtist: GetArtistUseCase,
	private val getTrackUseCase: GetTrackUseCase,
	private val getUserPlaylistsUseCase: GetUserPlaylistsUseCase,
	private val unlikeTrackUseCase: UnlikeTrackUseCase,
	private val likeTrackUseCase: LikeTrackUseCase,
	private val addTrackToPlaylistUseCase: AddTrackToPlaylistUseCase
) : ViewModel() {

	private val _uiState = MutableStateFlow(ArtistDetailUiState(isLoading = true))
	val uiState: StateFlow<ArtistDetailUiState> = _uiState.asStateFlow()

	init {
		createInitialState(artistId)
	}

	private fun createInitialState(artistId: String) {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, error = null) }

			getArtist(artistId.toLong())
				.onSuccess { artist ->
					_uiState.update { it.copy(
						artist = Artist(
							id = artistId,
							name = artist.name,
							avatarUrl = artist.avatarUrl,
							topTracks = null,
							monthlyListenersFormatted = artist.monthlyListenersFormatted
						)
					) }

					loadTracks(artist.topTracks ?: emptyList())
				}
				.onFailure { error ->
					_uiState.update { it.copy(isLoading = false, error = "Не удалось загрузить плейлист") }
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

	private suspend fun loadTracks(trackIds: List<Long>) {
		val tracks = mutableListOf<Track>()
		var hasError = false

		trackIds.forEach { id ->
			getTrackUseCase(id)
				.onSuccess { tracks.add(it) }
				.onFailure { hasError = true }
		}

		_uiState.update {
			it.copy(
				topTracks = tracks,
				isLoading = false,
				error = if (hasError && tracks.isEmpty()) "Не удалось загрузить треки" else it.error
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
				createInitialState(artistId)
			}
		}
	}

	fun addTrackToPlaylist(track: Track, playlist: Playlist) {
		viewModelScope.launch {
			val result = addTrackToPlaylistUseCase(playlist.id, track.id)

			if (result.isSuccess) {
				createInitialState(artistId)
			} else {
				_uiState.update { it.copy(error = result.exceptionOrNull()?.message) }
			}
		}
	}

	companion object {
		fun getViewModelFactory(artistId: String): ViewModelProvider.Factory = viewModelFactory {
			initializer {
				val artistRepository = Creator.provideArtistRepository()
				val trackRepository = Creator.provideTrackRepository()
				val libraryRepository = Creator.provideLibraryRepository()

				val getArtist = Creator.provideGetArtist(artistRepository)
				val getTrackUseCase = Creator.provideGetTrack(trackRepository)
				val getUserPlaylistsUseCase = Creator.provideGetUserPlaylists(libraryRepository)
				val unlikeTrackUseCase = Creator.provideUnlikeTrack(trackRepository)
				val likeTrackUseCase = Creator.provideLikeTrack(trackRepository)
				val addTrackToPlaylistUseCase = Creator.provideAddTrackToPlaylist(libraryRepository)

				ArtistDetailViewModel(
					artistId = artistId,
					getArtist = getArtist,
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