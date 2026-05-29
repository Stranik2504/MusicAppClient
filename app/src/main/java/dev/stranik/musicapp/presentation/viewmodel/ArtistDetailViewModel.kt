package dev.stranik.musicapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.stranik.musicapp.domain.model.Artist
import dev.stranik.musicapp.domain.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ArtistDetailUiState(
	val artist: Artist,
	val topTracks: List<Track> = emptyList(),
	val description: String = "Информация об артисте скоро будет расширена реальными данными.",
	val isLoading: Boolean = false,
	val error: String? = null
)

class ArtistDetailViewModel(
	private val artistId: String
) : ViewModel() {

	private val _uiState = MutableStateFlow(createInitialState(artistId))
	val uiState: StateFlow<ArtistDetailUiState> = _uiState.asStateFlow()

	private fun createInitialState(artistId: String): ArtistDetailUiState {
		val artistName = "Артист #$artistId"
		val topTracks = listOf(
			Track(
				id = "$artistId-track-1",
				title = "Главный сингл",
				artistName = artistName,
				albumTitle = "Лучшее",
				coverUrl = "",
				durationFormatted = "3:24"
			),
			Track(
				id = "$artistId-track-2",
				title = "Популярный трек",
				artistName = artistName,
				albumTitle = "Хиты",
				coverUrl = "",
				durationFormatted = "2:58"
			),
			Track(
				id = "$artistId-track-3",
				title = "Новая композиция",
				artistName = artistName,
				albumTitle = "Синглы",
				coverUrl = "",
				durationFormatted = "4:07"
			)
		)

		return ArtistDetailUiState(
			artist = Artist(
				id = artistId,
				name = artistName,
				avatarUrl = "",
				monthlyListenersFormatted = "1 250 000 слушателей в месяц"
			),
			topTracks = topTracks
		)
	}

	companion object {
		fun getViewModelFactory(artistId: String): ViewModelProvider.Factory = viewModelFactory {
			initializer {
				ArtistDetailViewModel(artistId = artistId)
			}
		}
	}
}