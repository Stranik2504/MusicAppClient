package dev.stranik.musicapp.data.model

import dev.stranik.musicapp.data.serializers.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class PlaylistDto(
    val id: Long,
    val title: String,
    val description: String?,
    val owner: PlaylistOwnerDto,
    val tracks: List<PlaylistTrackDto>,
    val isPublic: Boolean,
    val coverUrl: String?,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime,
)