package dev.stranik.musicapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(val username: String, val password: String)