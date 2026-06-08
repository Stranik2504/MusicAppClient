package dev.stranik.musicapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache_entries")
data class CacheEntryEntity(
    @PrimaryKey val key: String,
    val dataIds: List<String>,
    val cachedAt: Long = System.currentTimeMillis()
)
