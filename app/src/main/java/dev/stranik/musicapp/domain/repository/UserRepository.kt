package dev.stranik.musicapp.domain.repository

import dev.stranik.musicapp.domain.model.User

interface UserRepository {
    suspend fun getMe(): Result<User>
}