package dev.stranik.musicapp.domain.repository

import dev.stranik.musicapp.domain.model.Album

interface AlbumRepository {
    suspend fun getAlbum(albumId: Long): Result<Album>
}