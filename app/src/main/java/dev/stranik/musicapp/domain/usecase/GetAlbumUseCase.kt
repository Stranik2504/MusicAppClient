package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.model.Album
import dev.stranik.musicapp.domain.repository.AlbumRepository

class GetAlbumUseCase(
    private val albumRepository: AlbumRepository
) {
    suspend operator fun invoke(albumId: Long): Result<Album> {
        return albumRepository.getAlbum(albumId)
    }
}