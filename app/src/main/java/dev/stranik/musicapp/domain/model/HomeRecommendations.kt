package dev.stranik.musicapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class HomeRecommendations(
    val albums: List<Album>,
    val tracks: List<Track>
)
