package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.model.Artist
import dev.stranik.musicapp.domain.repository.ArtistRepository

class GetArtistUseCase(
    private val artistRepository: ArtistRepository
) {
    suspend operator fun invoke(artistId: Long): Result<Artist> {
        return artistRepository.getArtist(artistId)
    }
}