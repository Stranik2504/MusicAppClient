package dev.stranik.musicapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreatePlaylistRequestDto(
    val title: String,
    val description: String? = null,
    val isPublic: Boolean = true,
    val coverUrl: String? = null,
)