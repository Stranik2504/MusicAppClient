package dev.stranik.musicapp.data.repository

import dev.stranik.musicapp.data.local.dao.ArtistDao
import dev.stranik.musicapp.data.local.entity.toDomain
import dev.stranik.musicapp.data.local.entity.toEntity
import dev.stranik.musicapp.data.remote.ArtistApiService
import dev.stranik.musicapp.domain.model.Artist
import dev.stranik.musicapp.domain.repository.ArtistRepository
import dev.stranik.musicapp.presentation.mapper.toDomain
import io.ktor.http.HttpStatusCode

class ArtistRepositoryImpl(
    private val artistDao: ArtistDao
) : ArtistRepository {
    override suspend fun getArtist(artistId: Long): Result<Artist> = runCatching {
        val cachedArtist = artistDao.getArtistById(artistId.toString())
        if (cachedArtist != null && !isCacheExpired(cachedArtist.cachedAt)) {
            return@runCatching cachedArtist.toDomain()
        }

        val result = try {
            ArtistApiService.getArtist(artistId)
        } catch (e: Exception) {
            return@runCatching cachedArtist?.toDomain() ?: throw e
        }

        if (result.status != HttpStatusCode.OK) {
            return@runCatching cachedArtist?.toDomain()
                ?: throw Exception("Failed to fetch artist data: ${result.status}")
        }

        val artist = result.value.toDomain()
        artistDao.insertArtist(artist.toEntity())
        artist
    }

    private fun isCacheExpired(cachedAt: Long): Boolean {
        return System.currentTimeMillis() - cachedAt > CACHE_EXPIRATION_TIME
    }

    companion object {
        private const val CACHE_EXPIRATION_TIME = 24 * 60 * 60 * 1000L // 24 hours
    }
}
