package dev.stranik.musicapp.data.model

import dev.stranik.musicapp.data.serializers.LocalDateTimeSerializer
import java.time.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ArtistDetailsDto(
    val id: Long,
    val name: String,
    val bio: String,
    val avatarUrl: String,
    val country: String,
    val monthlyListeners: Int,
    val genres: List<GenreDto>,
    val topTracks: List<ArtistTrackDto>,
    val albums: List<ArtistAlbumDto>,
    val followersCount: Int,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
)