package dev.stranik.musicapp.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.stranik.musicapp.R
import dev.stranik.musicapp.domain.model.Album
import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.presentation.ui.component.ArtistCard
import dev.stranik.musicapp.presentation.ui.component.TrackItem
import dev.stranik.musicapp.presentation.viewmodel.SearchUiState
import dev.stranik.musicapp.presentation.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onTrackClick: (Track) -> Unit,
    onArtistClick: (String) -> Unit
) {
    val query by viewModel.query.collectAsState()
    val state by viewModel.uiState.collectAsState()
    val focusMgr = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.search_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        SearchBar(
            query = query,
            onQueryChange = viewModel::onQueryChange,
            onClear = {
                viewModel.onClearQuery()
                focusMgr.clearFocus()
            }
        )

        Spacer(Modifier.height(16.dp))

        when (val s = state) {
            is SearchUiState.Idle -> SearchIdleContent()
            is SearchUiState.Loading -> SearchLoadingContent()
            is SearchUiState.Success -> SearchResultsContent(
                viewModel = viewModel,
                state = s,
                onTrackClick = onTrackClick,
                onArtistClick = onArtistClick
            )

            is SearchUiState.Empty -> SearchEmptyContent(query = s.query)
            is SearchUiState.Error -> SearchErrorContent(message = s.message)
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(stringResource(R.string.search_placeholder)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.clear_search_cd))
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
}

@Composable
private fun SearchResultsContent(
    viewModel: SearchViewModel,
    state: SearchUiState.Success,
    onTrackClick: (Track) -> Unit,
    onArtistClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Треки
        if (state.tracks.isNotEmpty()) {
            item {
                SearchSectionTitle(stringResource(R.string.tracks_section))
            }
            items(state.tracks.take(5), key = { "track_" + it.id }) { track ->
                TrackItem(
                    track = track,
                    playlists = state.playlists,
                    onClick = { onTrackClick(track) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onToggleLike = { viewModel.toggleLike(track) },
                    onAddToPlaylist = { playlist -> viewModel.addTrackToPlaylist(track, playlist) }
                )
            }
        }

        // Артисты
        if (state.artists.isNotEmpty()) {
            item {
                SearchSectionTitle(stringResource(R.string.artists_section))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.artists, key = { "artist_" + it.id }) { artist ->
                        ArtistCard(
                            artist = artist,
                            onClick = { onArtistClick(artist.id) }
                        )
                    }
                }
            }
        }

        // Альбомы
        if (state.albums.isNotEmpty()) {
            item { SearchSectionTitle(stringResource(R.string.albums_section)) }
            items(state.albums, key = { "album_" + it.id }) { album ->
                AlbumListItem(album = album, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
private fun SearchSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun AlbumListItem(album: Album, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(shape = RoundedCornerShape(8.dp), modifier = Modifier.size(52.dp)) {
            AsyncImage(
                model = album.coverUrl,
                contentDescription = stringResource(
                    R.string.cover_name,
                    album.title
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Column {
            Text(album.title, style = MaterialTheme.typography.bodyLarge)
            Text("${album.artistName} · ${album.year}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun SearchIdleContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(R.string.search_idle_message),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun SearchLoadingContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun SearchEmptyContent(query: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(R.string.search_empty_message, query),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun SearchErrorContent(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            stringResource(R.string.error_format, message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
    }
}
