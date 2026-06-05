package dev.stranik.musicapp.data.model

import dev.stranik.musicapp.data.serializers.LocalDateTimeSerializer
import java.time.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class TrackDto(
    val id: Long,
    val title: String,
    val artist: AlbumArtistDto,
    val album: AlbumArtistDto?,
    val durationSec: Int,
    val playCount: Int,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
)