package dev.stranik.musicapp.data.repository

import dev.stranik.musicapp.data.local.dao.UserDao
import dev.stranik.musicapp.data.local.entity.toDomain
import dev.stranik.musicapp.data.local.entity.toEntity
import dev.stranik.musicapp.data.remote.UserApiService
import dev.stranik.musicapp.domain.model.User
import dev.stranik.musicapp.domain.repository.UserRepository
import dev.stranik.musicapp.presentation.mapper.toUi
import io.ktor.http.HttpStatusCode

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {
    override suspend fun getMe(): Result<User> = runCatching {
        val cachedMe = userDao.getMe()
        if (cachedMe != null && !isCacheExpired(cachedMe.cachedAt)) {
            return@runCatching cachedMe.toDomain()
        }

        val result = try {
            UserApiService.getMe()
        } catch (e: Exception) {
            return@runCatching cachedMe?.toDomain() ?: throw e
        }

        if (result.status != HttpStatusCode.OK) {
            return@runCatching cachedMe?.toDomain()
                ?: throw Exception("Failed to fetch user data: ${result.status}")
        }

        val user = result.value.toUi()
        userDao.insertUser(user.toEntity(isMe = true))
        user
    }

    private fun isCacheExpired(cachedAt: Long): Boolean {
        return System.currentTimeMillis() - cachedAt > CACHE_EXPIRATION_TIME
    }

    companion object {
        private const val CACHE_EXPIRATION_TIME = 24 * 60 * 60 * 1000L // 24 hours
    }
}
