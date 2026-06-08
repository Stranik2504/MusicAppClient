package dev.stranik.musicapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.stranik.musicapp.domain.model.Track

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artistName: String,
    val albumTitle: String,
    val coverUrl: String,
    val durationFormatted: String,
    val isLiked: Boolean,
    val cachedAt: Long = System.currentTimeMillis()
)

fun TrackEntity.toDomain() = Track(
    id = id,
    title = title,
    artistName = artistName,
    albumTitle = albumTitle,
    coverUrl = coverUrl,
    durationFormatted = durationFormatted,
    isLiked = isLiked
)

fun Track.toEntity() = TrackEntity(
    id = id,
    title = title,
    artistName = artistName,
    albumTitle = albumTitle,
    coverUrl = coverUrl,
    durationFormatted = durationFormatted,
    isLiked = isLiked
)
