package dev.stranik.musicapp.data.repository

import dev.stranik.musicapp.data.local.TokenManager
import dev.stranik.musicapp.data.model.LoginResponseDto
import dev.stranik.musicapp.data.remote.AuthApiService
import dev.stranik.musicapp.data.remote.KtorClient
import dev.stranik.musicapp.domain.model.UserRegistration
import dev.stranik.musicapp.domain.repository.AuthRepository
import dev.stranik.musicapp.presentation.mapper.toDto
import io.ktor.http.HttpStatusCode

class AuthRepositoryImpl(
    private val tokenManager: TokenManager
) : AuthRepository {
    override suspend fun login(username: String, password: String): Result<String> = runCatching {
        val result = AuthApiService.login(username, password)

        if (result.status != HttpStatusCode.OK)
            throw Exception("Login failed: ${result.status}")

        tokenManager.saveAccessToken(result.value.token)
        KtorClient.updateAccessToken(result.value.token)

        result.value.token
    }

    override suspend fun register(user: UserRegistration): Result<String> = runCatching {
        val result = AuthApiService.register(user.toDto())

        if (result.status != HttpStatusCode.Created)
            throw Exception("Registration failed: ${result.status}")

        return@runCatching result.value
    }

    override suspend fun logout() {
        tokenManager.clearTokens()
        KtorClient.clearTokens()
    }

    override suspend fun isLoggedIn(): Boolean {
        val token = tokenManager.getAccessToken()
        return token != null
    }
}