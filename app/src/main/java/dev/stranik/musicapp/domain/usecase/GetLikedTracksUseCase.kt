package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.domain.repository.LibraryRepository

class GetLikedTracksUseCase(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(): Result<List<Track>> {
        return repository.getLikedTracks()
    }
}
