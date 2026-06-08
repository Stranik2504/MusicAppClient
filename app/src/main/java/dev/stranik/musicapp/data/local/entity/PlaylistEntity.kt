package dev.stranik.musicapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.stranik.musicapp.domain.model.Playlist

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val coverUrl: String,
    val trackCount: Int,
    val isPublic: Boolean,
    val trackIds: List<String>,
    val cachedAt: Long = System.currentTimeMillis()
)

fun PlaylistEntity.toDomain() = Playlist(
    id = id,
    title = title,
    description = description,
    coverUrl = coverUrl,
    trackCount = trackCount,
    isPublic = isPublic,
    trackIds = trackIds
)

fun Playlist.toEntity() = PlaylistEntity(
    id = id,
    title = title,
    description = description,
    coverUrl = coverUrl,
    trackCount = trackCount,
    isPublic = isPublic,
    trackIds = trackIds
)
