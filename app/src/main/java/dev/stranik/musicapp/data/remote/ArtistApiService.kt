package dev.stranik.musicapp.data.remote

import dev.stranik.musicapp.data.model.ArtistDetailsDto
import dev.stranik.musicapp.data.model.Res
import io.ktor.client.call.body
import io.ktor.client.request.get

object ArtistApiService {
    suspend fun getArtist(artistId: Long): Res<ArtistDetailsDto> {
        val result = KtorClient.client.get("api/artists/$artistId")
        return Res(result.status, result.body())
    }
}