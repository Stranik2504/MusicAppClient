package dev.stranik.musicapp.presentation.mapper

import dev.stranik.musicapp.data.model.AlbumDto
import dev.stranik.musicapp.data.model.ArtistDetailsDto
import dev.stranik.musicapp.domain.model.Album
import dev.stranik.musicapp.domain.model.Artist
import dev.stranik.musicapp.domain.model.Playlist
import dev.stranik.musicapp.domain.model.Track

fun AlbumDto.toDomain() = Album(
    id = id.toString(),
    title = title,
    artistName = artist.name,
    tracks = tracks.map { it.id },
    coverUrl = coverUrl ?: "",
    year = createdAt.year,
    albumType = albumType ?: "Unknown"
)

fun Album.toPlaylist() = Playlist(
    id = id,
    title = title,
    description = "Альбом $title",
    coverUrl = coverUrl,
    trackCount = tracks.size,
    isPublic = false,
    trackIds = tracks.map { it.toString() }
)

fun ArtistDetailsDto.toDomain() = Artist(
    id = id.toString(),
    name = name,
    avatarUrl = avatarUrl,
    topTracks = topTracks.map { it.id },
    monthlyListenersFormatted = formatMonthlyListeners(monthlyListeners)
)

fun formatMonthlyListeners(monthlyListeners: Int): String {
    return when {
        monthlyListeners >= 1_000_000 -> "${monthlyListeners / 1_000_000} М"
        monthlyListeners >= 100_000 -> "${monthlyListeners / 100_000} тыс."
        else -> monthlyListeners.toString()
    }
}