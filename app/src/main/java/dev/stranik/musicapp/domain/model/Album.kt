package dev.stranik.musicapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val id: String,
    val title: String,
    val artistName: String,
    val tracks: List<Long>,
    val coverUrl: String,
    val year: Int,
    val albumType: String
)