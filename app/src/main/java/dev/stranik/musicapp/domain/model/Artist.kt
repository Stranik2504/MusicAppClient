package dev.stranik.musicapp.domain.model

data class Artist(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val monthlyListenersFormatted: String
)