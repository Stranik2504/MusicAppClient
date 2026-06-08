package dev.stranik.musicapp.data.repository

import dev.stranik.musicapp.data.local.dao.AlbumDao
import dev.stranik.musicapp.data.local.entity.toDomain
import dev.stranik.musicapp.data.local.entity.toEntity
import dev.stranik.musicapp.data.remote.AlbumsApiService
import dev.stranik.musicapp.domain.model.Album
import dev.stranik.musicapp.domain.repository.AlbumRepository
import dev.stranik.musicapp.presentation.mapper.toDomain
import io.ktor.http.HttpStatusCode

class AlbumRepositoryImpl(
    private val albumDao: AlbumDao
) : AlbumRepository {
    override suspend fun getAlbum(albumId: Long): Result<Album> = runCatching {
        val cachedAlbum = albumDao.getAlbumById(albumId.toString())
        if (cachedAlbum != null && !isCacheExpired(cachedAlbum.cachedAt)) {
            return@runCatching cachedAlbum.toDomain()
        }

        val albumRes = try {
            AlbumsApiService.getAlbum(albumId)
        } catch (e: Exception) {
            return@runCatching cachedAlbum?.toDomain() ?: throw e
        }

        if (albumRes.status != HttpStatusCode.OK) {
            return@runCatching cachedAlbum?.toDomain()
                ?: throw Exception("Failed to get album $albumId")
        }

        val album = albumRes.value.toDomain()
        albumDao.insertAlbum(album.toEntity())
        album
    }

    private fun isCacheExpired(cachedAt: Long): Boolean {
        return System.currentTimeMillis() - cachedAt > CACHE_EXPIRATION_TIME
    }

    companion object {
        private const val CACHE_EXPIRATION_TIME = 24 * 60 * 60 * 1000L // 24 hours
    }
}
