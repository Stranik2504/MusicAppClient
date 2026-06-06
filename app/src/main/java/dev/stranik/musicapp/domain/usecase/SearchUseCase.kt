package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.model.Album
import dev.stranik.musicapp.domain.model.Artist
import dev.stranik.musicapp.domain.model.SearchResult
import dev.stranik.musicapp.domain.model.Track



class SearchUseCase {
    operator fun invoke(query: String): SearchResult {
        // Возвращаем простые фейковые результаты, фильтруя по наличию query в title/name
        val lower = query.lowercase()
        val tracks = listOf(
            Track("s_t1", "Moonlight Serenade", "Luna Echo", "Midnight Dreams", "https://via.placeholder.com/300?text=Moonlight+Serenade", "3:45"),
            Track("s_t2", "City Lights", "City Sounds", "Urban Vibes", "https://via.placeholder.com/300?text=City+Lights", "3:28"),
            Track("s_t3", "Neon Rain", "Neon Pulse", "Electric Nights", "https://via.placeholder.com/300?text=Neon+Rain", "4:12")
        ).filter { it.title.lowercase().contains(lower) || it.artistName.lowercase().contains(lower) }

        val artists = listOf(
            Artist("s_a1", "Luna Echo", "https://via.placeholder.com/150?text=Luna+Echo", "2.5M"),
            Artist("s_a2", "Neon Pulse", "https://via.placeholder.com/150?text=Neon+Pulse", "1.8M"),
            Artist("s_a3", "City Sounds", "https://via.placeholder.com/150?text=City+Sounds", "3.2M")
        ).filter { it.name.lowercase().contains(lower) }

        val albums = listOf(
            Album("s_al1", "Midnight Dreams", "Luna Echo", listOf(1), "https://via.placeholder.com/300?text=Midnight+Dreams", 2023, "Album"),
            Album("s_al2", "Electric Nights", "Neon Pulse", listOf(1), "https://via.placeholder.com/300?text=Electric+Nights", 2023, "Album"),
            Album("s_al3", "Urban Vibes", "City Sounds", listOf(1), "https://via.placeholder.com/300?text=Urban+Vibes", 2024, "Album")
        ).filter { it.title.lowercase().contains(lower) || it.artistName.lowercase().contains(lower) }

        return SearchResult(tracks = tracks, artists = artists, albums = albums)
    }
}

