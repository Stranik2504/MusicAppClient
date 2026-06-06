package dev.stranik.musicapp.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import dev.stranik.musicapp.presentation.ui.component.AlbumCard
import dev.stranik.musicapp.presentation.ui.component.ArtistCard
import dev.stranik.musicapp.presentation.ui.component.TrackItem
import dev.stranik.musicapp.presentation.viewmodel.HomeUiState
import dev.stranik.musicapp.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onTrackClick: (Track) -> Unit,
    onArtistClick: (String) -> Unit,
    onAlbumClick: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> LoadingContent()
            state.error != null -> ErrorContent(
                message = state.error!!,
                onRetry = viewModel::loadHomeContent,
                onDismiss = viewModel::onErrorDismissed
            )

            else -> HomeContent(
                state = state,
                onTrackClick = onTrackClick,
                onArtistClick = onArtistClick,
                onAlbumClick = onAlbumClick
            )
        }
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    onTrackClick: (Track) -> Unit,
    onArtistClick: (String) -> Unit,
    onAlbumClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Добро пожаловать",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Рекомендованные альбомы
        if (state.featuredAlbums.isNotEmpty()) {
            item {
                SectionTitle(title = "Рекомендуем")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.featuredAlbums, key = { it.id }) { album ->
                        AlbumCard(
                            album = album,
                            onClick = { onAlbumClick(album.id) }
                        )
                    }
                }
            }
        }

        // Недавно воспроизведённые
        if (state.recentlyPlayed.isNotEmpty()) {
            item { SectionTitle(title = "Недавно прослушанное") }
            items(state.recentlyPlayed.take(5), key = { it.id }) { track ->
                TrackItem(
                    track = track,
                    onClick = { onTrackClick(track) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // Популярные артисты
        if (state.popularArtists.isNotEmpty()) {
            item {
                SectionTitle(title = "Популярные артисты")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.popularArtists, key = { it.id }) { artist ->
                        ArtistCard(
                            artist = artist,
                            onClick = { onArtistClick(artist.id) }
                        )
                    }
                }
            }
        }

        // Рекомендуемые треки
        if (state.recommendationTracks.isNotEmpty()) {
            item { SectionTitle(title = "Рекомендуемые треки") }
            items(state.recommendationTracks.take(20), key = { it.id }) { track ->
                TrackItem(
                    track = track,
                    onClick = { onTrackClick(track) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun LoadingContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ошибка загрузки",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onRetry) { Text("Повторить") }
    }
}