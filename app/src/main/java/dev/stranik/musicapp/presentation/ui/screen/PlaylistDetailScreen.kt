package dev.stranik.musicapp.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.presentation.ui.component.TrackItem
import dev.stranik.musicapp.presentation.viewmodel.PlaylistDetailViewModel

@Composable
fun PlaylistDetailScreen(
	viewModel: PlaylistDetailViewModel,
	onBack: () -> Unit,
	onTrackClick: (Track) -> Unit
) {
	val state by viewModel.uiState.collectAsState()

	LazyColumn(
		modifier = Modifier.fillMaxSize(),
		contentPadding = PaddingValues(16.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		item {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				IconButton(onClick = onBack) {
					Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
				}
				Text(
					text = "Плейлист",
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.SemiBold
				)
				Spacer(modifier = Modifier.height(48.dp))
			}
		}

		item {
			Card(modifier = Modifier.fillMaxWidth()) {
				Column(modifier = Modifier.padding(16.dp)) {
					Text(
						text = state.playlist.title,
						style = MaterialTheme.typography.headlineMedium,
						fontWeight = FontWeight.Bold
					)
					Spacer(Modifier.height(8.dp))
					Text(
						text = "${state.playlist.trackCount} треков · ${if (state.playlist.isPublic) "публичный" else "приватный"}",
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
					Spacer(Modifier.height(12.dp))
					Text(
						text = state.description,
						style = MaterialTheme.typography.bodyMedium
					)
				}
			}
		}

		if (state.tracks.isNotEmpty()) {
			item {
				Text(
					text = "Треки",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.SemiBold
				)
			}
			items(state.tracks, key = { it.id }) { track ->
				TrackItem(
					track = track,
					onClick = { onTrackClick(track) }
				)
			}
		} else {
			item {
				Button(onClick = onBack) {
					Text("Вернуться назад")
				}
			}
		}
	}
}