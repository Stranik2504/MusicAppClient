package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetUserPlaylistsUseCase {
    operator fun invoke(): Flow<List<Playlist>> = flowOf(
        listOf(
            Playlist(
                id = "p1",
                title = "Favorite Vibes",
                coverUrl = "https://via.placeholder.com/300?text=Favorite+Vibes",
                trackCount = 24,
                isPublic = true
            ),
            Playlist(
                id = "p2",
                title = "Chillout",
                coverUrl = "https://via.placeholder.com/300?text=Chillout",
                trackCount = 18,
                isPublic = false
            ),
            Playlist(
                id = "p3",
                title = "Road Trip",
                coverUrl = "https://via.placeholder.com/300?text=Road+Trip",
                trackCount = 42,
                isPublic = true
            )
        )
    )
}

