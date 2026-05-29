package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetRecentlyPlayedUseCase {
    operator fun invoke(): Flow<List<Track>> = flowOf(
        listOf(
            Track(
                id = "t1",
                title = "Moonlight Serenade",
                artistName = "Luna Echo",
                albumTitle = "Midnight Dreams",
                coverUrl = "https://via.placeholder.com/300?text=Moonlight+Serenade",
                durationFormatted = "3:45",
                isLiked = true
            ),
            Track(
                id = "t2",
                title = "Neon Rain",
                artistName = "Neon Pulse",
                albumTitle = "Electric Nights",
                coverUrl = "https://via.placeholder.com/300?text=Neon+Rain",
                durationFormatted = "4:12",
                isLiked = false
            ),
            Track(
                id = "t3",
                title = "City Lights",
                artistName = "City Sounds",
                albumTitle = "Urban Vibes",
                coverUrl = "https://via.placeholder.com/300?text=City+Lights",
                durationFormatted = "3:28",
                isLiked = true
            ),
            Track(
                id = "t4",
                title = "Star Dust",
                artistName = "Space Waves",
                albumTitle = "Cosmic Journey",
                coverUrl = "https://via.placeholder.com/300?text=Star+Dust",
                durationFormatted = "5:01",
                isLiked = false
            ),
            Track(
                id = "t5",
                title = "Echoes",
                artistName = "Luna Echo",
                albumTitle = "Midnight Dreams",
                coverUrl = "https://via.placeholder.com/300?text=Echoes",
                durationFormatted = "4:33",
                isLiked = true
            )
        )
    )
}

