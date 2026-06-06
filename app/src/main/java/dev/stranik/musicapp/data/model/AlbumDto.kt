package dev.stranik.musicapp.data.model

import dev.stranik.musicapp.data.serializers.LocalDateTimeSerializer
import java.time.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class AlbumDto(
    val id: Long,
    val title: String,
    val artist: AlbumArtistDto,
    @Serializable(with = LocalDateTimeSerializer::class)
    val releaseDate: LocalDateTime,
    val tracks: List<AlbumTrackDto>,
    val coverUrl: String?,
    val genre: GenreDto,
    val albumType: String?,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
)