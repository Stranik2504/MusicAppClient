package dev.stranik.musicapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(val token: String)