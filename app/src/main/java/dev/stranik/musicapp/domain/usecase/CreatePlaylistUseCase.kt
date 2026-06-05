package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.model.Playlist
import dev.stranik.musicapp.domain.repository.LibraryRepository

class CreatePlaylistUseCase(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(title: String, description: String? = null): Result<Playlist> {
        return repository.createPlaylist(title, description)
    }
}
