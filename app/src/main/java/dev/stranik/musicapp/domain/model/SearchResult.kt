package dev.stranik.musicapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    val tracks: List<Track> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val albums: List<Album> = emptyList()
)