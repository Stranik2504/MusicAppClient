package dev.stranik.musicapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val username: String,
    val email: String,
    val password: String
)