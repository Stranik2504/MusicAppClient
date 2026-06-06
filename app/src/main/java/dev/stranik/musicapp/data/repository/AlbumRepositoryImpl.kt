package dev.stranik.musicapp.data.repository

import dev.stranik.musicapp.data.remote.AlbumsApiService
import dev.stranik.musicapp.domain.model.Album
import dev.stranik.musicapp.domain.repository.AlbumRepository
import dev.stranik.musicapp.presentation.mapper.toDomain
import io.ktor.http.HttpStatusCode

class AlbumRepositoryImpl : AlbumRepository {
    override suspend fun getAlbum(albumId: Long): Result<Album> = runCatching {
        val albumRes = AlbumsApiService.getAlbum(albumId)

        if (albumRes.status != HttpStatusCode.OK)
            throw Exception("Failed to get liked ids")

        albumRes.value.toDomain()
    }
}