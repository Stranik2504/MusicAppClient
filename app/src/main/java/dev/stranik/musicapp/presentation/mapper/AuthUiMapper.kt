package dev.stranik.musicapp.presentation.mapper

import dev.stranik.musicapp.data.model.UserDto
import dev.stranik.musicapp.domain.model.UserRegistration

fun UserRegistration.toDto() = UserDto(
    username = username,
    email = email,
    password = password
)