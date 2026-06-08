package dev.stranik.musicapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.stranik.musicapp.data.local.entity.CacheEntryEntity

@Dao
interface CacheEntryDao {
    @Query("SELECT * FROM cache_entries WHERE `key` = :key")
    suspend fun getByKey(key: String): CacheEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: CacheEntryEntity)

    @Query("DELETE FROM cache_entries WHERE `key` = :key")
    suspend fun deleteByKey(key: String)
}
