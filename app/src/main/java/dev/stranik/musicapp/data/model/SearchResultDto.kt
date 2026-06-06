package dev.stranik.musicapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchResultDto(
    val tracks: List<TrackDto>?,
    val albums: List<AlbumDto>?,
    val artists: List<ArtistDto>?,
    val playlists: List<PlaylistDto>?,
)