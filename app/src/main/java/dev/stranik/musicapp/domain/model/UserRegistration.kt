package dev.stranik.musicapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserRegistration(
    val username: String,
    val email: String,
    val password: String
)