package dev.stranik.musicapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val email: String,
    val avatarUrl: String?,
)