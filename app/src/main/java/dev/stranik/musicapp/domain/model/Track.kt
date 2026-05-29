package dev.stranik.musicapp.domain.model

data class Track (
    val id: String,
    val title: String,
    val artistName: String,
    val albumTitle: String,
    val coverUrl: String,
    val durationFormatted: String,
    val isLiked: Boolean = false
)