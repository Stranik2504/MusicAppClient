package dev.stranik.musicapp.data.remote

import dev.stranik.musicapp.data.model.Res
import dev.stranik.musicapp.data.model.UserDto
import dev.stranik.musicapp.data.model.UserInfoDto
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

object UserApiService {
    suspend fun getMe(): Res<UserInfoDto> {
        val result = KtorClient.client.get("api/users/me") {
        }

        return Res(result.status, result.body())
    }
}