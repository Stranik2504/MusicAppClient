package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.repository.LibraryRepository

class RemoveTrackFromPlaylistUseCase(
    private val libraryRepository: LibraryRepository
) {
    suspend operator fun invoke(playlistId: String, trackId: String): Result<Unit> {
        return libraryRepository.removeTrackFromPlaylist(playlistId, trackId)
    }
}
