package dev.stranik.musicapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistTrackDto(
    val id: Long,
    val trackId: Long,
    val position: Int,
)