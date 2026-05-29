package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.model.Album
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetNewReleasesUseCase {
    operator fun invoke(): Flow<List<Album>> = flowOf(
        listOf(
            Album(
                id = "n1",
                title = "Sunset Boulevard",
                artistName = "Harmony Dreams",
                coverUrl = "https://via.placeholder.com/300?text=Sunset+Boulevard",
                year = 2024,
                albumType = "Album"
            ),
            Album(
                id = "n2",
                title = "Digital Horizons",
                artistName = "Neon Pulse",
                coverUrl = "https://via.placeholder.com/300?text=Digital+Horizons",
                year = 2024,
                albumType = "EP"
            ),
            Album(
                id = "n3",
                title = "Gravity Wells",
                artistName = "Space Waves",
                coverUrl = "https://via.placeholder.com/300?text=Gravity+Wells",
                year = 2024,
                albumType = "Album"
            ),
            Album(
                id = "n4",
                title = "Resonance",
                artistName = "City Sounds",
                coverUrl = "https://via.placeholder.com/300?text=Resonance",
                year = 2024,
                albumType = "Single"
            )
        )
    )
}

