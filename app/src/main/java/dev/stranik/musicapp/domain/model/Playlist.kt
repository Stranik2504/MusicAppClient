package dev.stranik.musicapp.domain.model

data class Playlist(
    val id: String,
    val title: String,
    val coverUrl: String,
    val trackCount: Int,
    val isPublic: Boolean
)