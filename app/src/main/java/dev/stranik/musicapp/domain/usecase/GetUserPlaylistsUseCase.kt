package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.model.Playlist
import dev.stranik.musicapp.domain.repository.LibraryRepository

class GetUserPlaylistsUseCase(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(): Result<List<Playlist>> {
        return repository.getUserPlaylists()
    }
}
