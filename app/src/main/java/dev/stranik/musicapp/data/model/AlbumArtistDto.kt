package dev.stranik.musicapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AlbumArtistDto(
    val id: Long,
    val name: String,
)