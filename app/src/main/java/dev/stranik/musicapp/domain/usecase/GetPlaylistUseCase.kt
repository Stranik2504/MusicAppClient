package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.model.Playlist
import dev.stranik.musicapp.domain.repository.LibraryRepository

class GetPlaylistUseCase(
    private val libraryRepository: LibraryRepository
) {
    suspend operator fun invoke(playlistId: String): Result<Playlist> {
        return libraryRepository.getPlaylist(playlistId)
    }
}
