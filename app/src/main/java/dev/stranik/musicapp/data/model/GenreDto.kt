package dev.stranik.musicapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GenreDto(
    val name: String,
    val slug: String,
)