package dev.stranik.musicapp.data.model

import dev.stranik.musicapp.data.serializers.LocalDateTimeSerializer
import java.time.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ListeningHistoryDto(
    val trackId: Long,
    val playedSec: Int,
    @Serializable(with = LocalDateTimeSerializer::class)
    val playedAt: LocalDateTime
)