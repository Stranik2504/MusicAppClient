package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.model.Artist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetPopularArtistsUseCase {
    operator fun invoke(): Flow<List<Artist>> = flowOf(
        listOf(
            Artist(
                id = "a1",
                name = "Luna Echo",
                avatarUrl = "https://via.placeholder.com/150?text=Luna+Echo",
                monthlyListenersFormatted = "2.5M"
            ),
            Artist(
                id = "a2",
                name = "Neon Pulse",
                avatarUrl = "https://via.placeholder.com/150?text=Neon+Pulse",
                monthlyListenersFormatted = "1.8M"
            ),
            Artist(
                id = "a3",
                name = "City Sounds",
                avatarUrl = "https://via.placeholder.com/150?text=City+Sounds",
                monthlyListenersFormatted = "3.2M"
            ),
            Artist(
                id = "a4",
                name = "Space Waves",
                avatarUrl = "https://via.placeholder.com/150?text=Space+Waves",
                monthlyListenersFormatted = "1.5M"
            ),
            Artist(
                id = "a5",
                name = "Harmony Dreams",
                avatarUrl = "https://via.placeholder.com/150?text=Harmony+Dreams",
                monthlyListenersFormatted = "2.1M"
            )
        )
    )
}

