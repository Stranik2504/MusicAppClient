package dev.stranik.musicapp.domain.model

data class Album(
    val id: String,
    val title: String,
    val artistName: String,
    val coverUrl: String,
    val year: Int,
    val albumType: String
)