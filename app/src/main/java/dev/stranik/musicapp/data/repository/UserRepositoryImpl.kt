package dev.stranik.musicapp.data.repository

import dev.stranik.musicapp.data.remote.KtorClient
import dev.stranik.musicapp.data.remote.UserApiService
import dev.stranik.musicapp.domain.model.User
import dev.stranik.musicapp.domain.repository.UserRepository
import dev.stranik.musicapp.presentation.mapper.toUi
import io.ktor.http.HttpStatusCode

class UserRepositoryImpl : UserRepository {
    override suspend fun getMe(): Result<User> = runCatching {
        val result = UserApiService.getMe()

        if (result.status != HttpStatusCode.OK)
            throw Exception("Failed to fetch user data: ${result.status}")

        result.value.toUi()
    }
}