package dev.stranik.musicapp.data.repository

import dev.stranik.musicapp.data.remote.ArtistApiService
import dev.stranik.musicapp.domain.model.Artist
import dev.stranik.musicapp.domain.repository.ArtistRepository
import dev.stranik.musicapp.presentation.mapper.toDomain
import io.ktor.http.HttpStatusCode

class ArtistRepositoryImpl : ArtistRepository {
    override suspend fun getArtist(artistId: Long): Result<Artist> = runCatching {
        val result = ArtistApiService.getArtist(artistId)

        if (result.status != HttpStatusCode.OK)
            throw Exception("Failed to fetch artist data: ${result.status}")

        result.value.toDomain()
    }
}