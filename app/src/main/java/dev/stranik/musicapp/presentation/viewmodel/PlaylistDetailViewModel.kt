package dev.stranik.musicapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.stranik.musicapp.domain.model.Playlist
import dev.stranik.musicapp.domain.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PlaylistDetailUiState(
	val playlist: Playlist,
	val tracks: List<Track> = emptyList(),
	val description: String = "Содержимое плейлиста будет подключено к данным позже.",
	val isLoading: Boolean = false,
	val error: String? = null
)

class PlaylistDetailViewModel(
	private val playlistId: String
) : ViewModel() {

	private val _uiState = MutableStateFlow(createInitialState(playlistId))
	val uiState: StateFlow<PlaylistDetailUiState> = _uiState.asStateFlow()

	private fun createInitialState(playlistId: String): PlaylistDetailUiState {
		val playlistTitle = "Плейлист #$playlistId"
		val tracks = listOf(
			Track(
				id = "$playlistId-track-1",
				title = "Открывающий трек",
				artistName = "Разные исполнители",
				albumTitle = playlistTitle,
				coverUrl = "",
				durationFormatted = "3:41"
			),
			Track(
				id = "$playlistId-track-2",
				title = "Трек для настроения",
				artistName = "Разные исполнители",
				albumTitle = playlistTitle,
				coverUrl = "",
				durationFormatted = "4:15"
			),
			Track(
				id = "$playlistId-track-3",
				title = "Финальный трек",
				artistName = "Разные исполнители",
				albumTitle = playlistTitle,
				coverUrl = "",
				durationFormatted = "2:52"
			)
		)

		return PlaylistDetailUiState(
			playlist = Playlist(
				id = playlistId,
				title = playlistTitle,
				coverUrl = "",
				trackCount = tracks.size,
				isPublic = playlistId.hashCode() % 2 == 0
			),
			tracks = tracks
		)
	}

	companion object {
		fun getViewModelFactory(playlistId: String): ViewModelProvider.Factory = viewModelFactory {
			initializer {
				PlaylistDetailViewModel(playlistId = playlistId)
			}
		}
	}
}