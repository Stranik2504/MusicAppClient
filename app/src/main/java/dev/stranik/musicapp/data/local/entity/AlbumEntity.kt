package dev.stranik.musicapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.stranik.musicapp.domain.model.Album

@Entity(tableName = "albums")
data class AlbumEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artistName: String,
    val tracks: List<Long>,
    val coverUrl: String,
    val year: Int,
    val albumType: String,
    val cachedAt: Long = System.currentTimeMillis()
)

fun AlbumEntity.toDomain() = Album(
    id = id,
    title = title,
    artistName = artistName,
    tracks = tracks,
    coverUrl = coverUrl,
    year = year,
    albumType = albumType
)

fun Album.toEntity() = AlbumEntity(
    id = id,
    title = title,
    artistName = artistName,
    tracks = tracks,
    coverUrl = coverUrl,
    year = year,
    albumType = albumType
)
