package dev.stranik.musicapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ArtistAlbumDto(
    val id: Long,
    val title: String,
)