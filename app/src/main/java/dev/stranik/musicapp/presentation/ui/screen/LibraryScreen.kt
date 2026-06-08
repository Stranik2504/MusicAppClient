package dev.stranik.musicapp.presentation.ui.screen

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.stranik.musicapp.R
import dev.stranik.musicapp.domain.model.Playlist
import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.presentation.ui.component.TrackItem
import dev.stranik.musicapp.presentation.viewmodel.LibraryTab
import dev.stranik.musicapp.presentation.viewmodel.LibraryViewModel

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    onPlaylistClick: (String) -> Unit,
    onTrackClick: (Track) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(LibraryTab.PLAYLISTS) }
    var showCreateDialog by remember { mutableStateOf(false) }

    if (showCreateDialog) {
        CreatePlaylistDialog(
            onConfirm = { title ->
                viewModel.onCreatePlaylist(title)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LibraryHeader(
            onCreatePlaylist = { showCreateDialog = true }
        )

        TabRow(selectedTabIndex = selectedTab.ordinal) {
            LibraryTab.entries.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = { Text(stringResource(tab.labelRes)) }
                )
            }
        }

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            state.error != null -> ErrorState(
                message = state.error!!,
                onRetry = viewModel::loadLibrary
            )

            else -> when (selectedTab) {
                LibraryTab.PLAYLISTS -> PlaylistsTab(
                    playlists = state.playlists,
                    onPlaylistClick = onPlaylistClick
                )

                LibraryTab.LIKED -> LikedTracksTab(
                    tracks = state.likedTracks,
                    playlists = state.playlists,
                    onTrackClick = onTrackClick,
                    onToggleLike = { viewModel.toggleLike(it) },
                    onAddToPlaylist = { track, playlist -> viewModel.addTrackToPlaylist(track, playlist) }
                )
            }
        }
    }
}

@Composable
private fun LibraryHeader(onCreatePlaylist: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.library_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onCreatePlaylist) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.create_playlist_cd))
        }
    }
}

@Composable
private fun PlaylistsTab(
    playlists: List<Playlist>,
    onPlaylistClick: (String) -> Unit
) {
    if (playlists.isEmpty()) {
        EmptyState(
            icon = Icons.Default.LibraryMusic,
            message = stringResource(R.string.no_playlists_message)
        )
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(playlists, key = { it.id }) { playlist ->
            PlaylistItem(
                playlist = playlist,
                onClick = { onPlaylistClick(playlist.id) }
            )
        }
    }
}

@Composable
private fun PlaylistItem(playlist: Playlist, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .then(Modifier.clickable { onClick() }),
        headlineContent = { Text(playlist.title, fontWeight = FontWeight.Medium) },
        supportingContent = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!playlist.isPublic) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = stringResource(R.string.private_cd),
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(stringResource(R.string.track_count_format, playlist.trackCount))
            }
        },
        leadingContent = {
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(52.dp)
            ) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.PlaylistPlay, contentDescription = null)
                }
            }
        },
        trailingContent = {
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
private fun LikedTracksTab(
    tracks: List<Track>,
    playlists: List<Playlist>,
    onTrackClick: (Track) -> Unit,
    onToggleLike: (Track) -> Unit,
    onAddToPlaylist: (Track, Playlist) -> Unit
) {
    if (tracks.isEmpty()) {
        EmptyState(
            icon = Icons.Default.FavoriteBorder,
            message = stringResource(R.string.no_liked_tracks_message)
        )
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(tracks, key = { it.id }) { track ->
            TrackItem(
                track = track,
                onClick = { onTrackClick(track) },
                onToggleLike = { onToggleLike(track) },
                playlists = playlists,
                onAddToPlaylist = { playlist -> onAddToPlaylist(track, playlist) }
            )
        }
    }
}

@Composable
private fun EmptyState(icon: androidx.compose.ui.graphics.vector.ImageVector, message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Text(message, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text(stringResource(R.string.retry)) }
    }
}


@Composable
private fun CreatePlaylistDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.new_playlist_dialog_title)) },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text(stringResource(R.string.playlist_name_placeholder)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (title.isNotBlank()) onConfirm(title.trim()) },
                enabled = title.isNotBlank()
            ) { Text(stringResource(R.string.create_button)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel_button)) }
        }
    )
}
