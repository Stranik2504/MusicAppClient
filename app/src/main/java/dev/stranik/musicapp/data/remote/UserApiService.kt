package dev.stranik.musicapp.data.remote

import dev.stranik.musicapp.data.model.Res
import dev.stranik.musicapp.data.model.UserInfoDto
import io.ktor.client.call.body
import io.ktor.client.request.get

object UserApiService {
    suspend fun getMe(): Res<UserInfoDto> {
        val result = KtorClient.client.get("api/users/me")
        return Res(result.status, result.body())
    }

    suspend fun getLikedTrackIds(): Res<List<Long>> {
        val result = KtorClient.client.get("api/users/liked")
        return Res(result.status, result.body())
    }

    suspend fun getUserPlaylistIds(): Res<List<Long>> {
        val result = KtorClient.client.get("api/users/playlists")
        return Res(result.status, result.body())
    }
}
