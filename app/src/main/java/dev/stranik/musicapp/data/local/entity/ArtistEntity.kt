package dev.stranik.musicapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.stranik.musicapp.domain.model.Artist

@Entity(tableName = "artists")
data class ArtistEntity(
    @PrimaryKey val id: String,
    val name: String,
    val avatarUrl: String,
    val topTracks: List<Long>?,
    val monthlyListenersFormatted: String,
    val cachedAt: Long = System.currentTimeMillis()
)

fun ArtistEntity.toDomain() = Artist(
    id = id,
    name = name,
    avatarUrl = avatarUrl,
    topTracks = topTracks,
    monthlyListenersFormatted = monthlyListenersFormatted
)

fun Artist.toEntity() = ArtistEntity(
    id = id,
    name = name,
    avatarUrl = avatarUrl,
    topTracks = topTracks,
    monthlyListenersFormatted = monthlyListenersFormatted
)
