package dev.stranik.musicapp.domain.repository

import dev.stranik.musicapp.domain.model.UserRegistration

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<String>
    suspend fun register(user: UserRegistration): Result<String>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}