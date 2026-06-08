package dev.stranik.musicapp.presentation.mapper

import dev.stranik.musicapp.data.model.PlaylistDto
import dev.stranik.musicapp.data.model.TrackDto
import dev.stranik.musicapp.domain.model.Playlist
import dev.stranik.musicapp.domain.model.Track

fun TrackDto.toDomain(isLiked: Boolean = false, coverUrl: String = "") = Track(
    id = id.toString(),
    title = title,
    artistName = artist.name,
    albumTitle = album?.name ?: "",
    coverUrl = coverUrl,
    durationFormatted = formatDuration(durationSec),
    isLiked = isLiked
)

fun PlaylistDto.toDomain() = Playlist(
    id = id.toString(),
    title = title,
    coverUrl = coverUrl ?: "",
    trackCount = tracks.size,
    isPublic = isPublic,
    trackIds = tracks.map { it.trackId.toString() }
)

private fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%d:%02d".format(minutes, remainingSeconds)
}
