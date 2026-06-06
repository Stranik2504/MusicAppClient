package dev.stranik.musicapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AddTrackRequestDto(
    val trackId: Long,
    val position: Int? = null,
)