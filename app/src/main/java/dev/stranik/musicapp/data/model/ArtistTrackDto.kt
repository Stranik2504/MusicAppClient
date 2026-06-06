package dev.stranik.musicapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ArtistTrackDto(
    val id: Long,
    val title: String,
    val durationSec: Int,
)
