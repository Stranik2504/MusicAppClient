package dev.stranik.musicapp.data.model

import dev.stranik.musicapp.data.serializers.LocalDateTimeSerializer
import java.time.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ArtistDto(
    val id: Long,
    val name: String,
    val bio: String,
    val avatarUrl: String,
    val country: String,
    val monthlyListeners: Int,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
)
