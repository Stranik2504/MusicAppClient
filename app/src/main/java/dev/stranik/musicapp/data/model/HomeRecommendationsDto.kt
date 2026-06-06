package dev.stranik.musicapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class HomeRecommendationsDto(
    val albums: List<AlbumDto>,
    val tracks: List<TrackDto>,
)
