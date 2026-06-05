package dev.stranik.musicapp.presentation.mapper

import dev.stranik.musicapp.data.model.UserInfoDto
import dev.stranik.musicapp.domain.model.User

fun UserInfoDto.toUi() = User(
    username = username,
    email = email,
    avatarUrl = avatarUrl
)
