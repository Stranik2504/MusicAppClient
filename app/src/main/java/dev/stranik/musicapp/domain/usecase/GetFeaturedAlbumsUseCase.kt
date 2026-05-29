package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.model.Album
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetFeaturedAlbumsUseCase {
    operator fun invoke(): Flow<List<Album>> = flowOf(
        listOf(
            Album(
                id = "1",
                title = "Midnight Dreams",
                artistName = "Luna Echo",
                coverUrl = "https://via.placeholder.com/300?text=Midnight+Dreams",
                year = 2023,
                albumType = "Album"
            ),
            Album(
                id = "2",
                title = "Electric Nights",
                artistName = "Neon Pulse",
                coverUrl = "https://via.placeholder.com/300?text=Electric+Nights",
                year = 2023,
                albumType = "Album"
            ),
            Album(
                id = "3",
                title = "Urban Vibes",
                artistName = "City Sounds",
                coverUrl = "https://via.placeholder.com/300?text=Urban+Vibes",
                year = 2024,
                albumType = "Album"
            ),
            Album(
                id = "4",
                title = "Cosmic Journey",
                artistName = "Space Waves",
                coverUrl = "https://via.placeholder.com/300?text=Cosmic+Journey",
                year = 2024,
                albumType = "Album"
            )
        )
    )
}

