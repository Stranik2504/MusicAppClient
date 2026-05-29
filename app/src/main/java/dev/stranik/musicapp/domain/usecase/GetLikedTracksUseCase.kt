package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetLikedTracksUseCase {
    operator fun invoke(): Flow<List<Track>> = flowOf(
        listOf(
            Track(
                id = "lt1",
                title = "Moonlight Serenade",
                artistName = "Luna Echo",
                albumTitle = "Midnight Dreams",
                coverUrl = "https://via.placeholder.com/300?text=Moonlight+Serenade",
                durationFormatted = "3:45",
                isLiked = true
            ),
            Track(
                id = "lt2",
                title = "Echoes",
                artistName = "Luna Echo",
                albumTitle = "Midnight Dreams",
                coverUrl = "https://via.placeholder.com/300?text=Echoes",
                durationFormatted = "4:33",
                isLiked = true
            ),
            Track(
                id = "lt3",
                title = "City Lights",
                artistName = "City Sounds",
                albumTitle = "Urban Vibes",
                coverUrl = "https://via.placeholder.com/300?text=City+Lights",
                durationFormatted = "3:28",
                isLiked = true
            )
        )
    )
}

