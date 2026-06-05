package dev.stranik.musicapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistOwnerDto(
    val id: Long,
    val username: String,
)