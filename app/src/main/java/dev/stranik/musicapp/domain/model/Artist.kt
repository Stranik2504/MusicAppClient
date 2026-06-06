package dev.stranik.musicapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Artist(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val monthlyListenersFormatted: String
)