package dev.stranik.musicapp.data.remote

import dev.stranik.musicapp.data.model.AddTrackRequestDto
import dev.stranik.musicapp.data.model.CreatePlaylistRequestDto
import dev.stranik.musicapp.data.model.PlaylistDto
import dev.stranik.musicapp.data.model.Res
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

object PlaylistApiService {
    suspend fun getPlaylist(playlistId: Long): Res<PlaylistDto> {
        val result = KtorClient.client.get("api/playlists/$playlistId")
        return Res(result.status, result.body())
    }

    suspend fun createPlaylist(request: CreatePlaylistRequestDto): Res<PlaylistDto> {
        val result = KtorClient.client.post("api/playlists/create") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return Res(result.status, result.body())
    }

    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long): Res<Unit> {
        val result = KtorClient.client.post("api/playlists/$playlistId/tracks") {
            contentType(ContentType.Application.Json)
            setBody(AddTrackRequestDto(trackId = trackId))
        }
        return Res(result.status, Unit)
    }

    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long): Res<Unit> {
        val result = KtorClient.client.delete("api/playlists/$playlistId/tracks/$trackId")
        return Res(result.status, Unit)
    }
}
