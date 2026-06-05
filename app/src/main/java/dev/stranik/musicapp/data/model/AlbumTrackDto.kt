package dev.stranik.musicapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AlbumTrackDto(
    val id: Long,
    val title: String,
    val durationSec: Int,
    val trackNumber: Int,
)