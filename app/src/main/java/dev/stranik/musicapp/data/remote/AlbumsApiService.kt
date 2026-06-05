package dev.stranik.musicapp.data.remote

import dev.stranik.musicapp.data.model.AlbumDto
import dev.stranik.musicapp.data.model.Res
import io.ktor.client.call.body
import io.ktor.client.request.get

object AlbumsApiService {
    suspend fun getAlbum(albumId: Long): Res<AlbumDto> {
        val result = KtorClient.client.get("api/albums/$albumId")
        return Res(result.status, result.body())
    }
}