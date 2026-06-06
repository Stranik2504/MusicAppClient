package dev.stranik.musicapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TrackRecommendationsDto(
    val tracks: List<TrackDto>,
)