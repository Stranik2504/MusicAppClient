package dev.stranik.musicapp.data.remote

import android.util.Log
import dev.stranik.musicapp.data.model.Res
import dev.stranik.musicapp.data.model.UserInfoDto
import io.ktor.client.call.body
import io.ktor.client.request.get

object UserApiService {
    suspend fun getMe(): Res<UserInfoDto> {
        val result = KtorClient.client.get("api/users/me") {
        }

        return Res(result.status, result.body())
    }
}