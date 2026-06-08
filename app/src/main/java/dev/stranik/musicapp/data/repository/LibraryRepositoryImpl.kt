package dev.stranik.musicapp.data.repository

import dev.stranik.musicapp.data.local.dao.CacheEntryDao
import dev.stranik.musicapp.data.local.dao.PlaylistDao
import dev.stranik.musicapp.data.local.dao.TrackDao
import dev.stranik.musicapp.data.local.entity.CacheEntryEntity
import dev.stranik.musicapp.data.local.entity.toDomain
import dev.stranik.musicapp.data.local.entity.toEntity
import dev.stranik.musicapp.data.model.CreatePlaylistRequestDto
import dev.stranik.musicapp.data.remote.AlbumsApiService
import dev.stranik.musicapp.data.remote.PlaylistApiService
import dev.stranik.musicapp.data.remote.TrackApiService
import dev.stranik.musicapp.data.remote.UserApiService
import dev.stranik.musicapp.domain.model.Playlist
import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.domain.repository.LibraryRepository
import dev.stranik.musicapp.presentation.mapper.toDomain
import io.ktor.http.HttpStatusCode

class LibraryRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val trackDao: TrackDao,
    private val cacheEntryDao: CacheEntryDao
) : LibraryRepository {

    override suspend fun getLikedTracks(): Result<List<Track>> = runCatching {
        val cacheKey = "liked_tracks"
        val cachedEntry = cacheEntryDao.getByKey(cacheKey)
        
        if (cachedEntry != null && !isCacheExpired(cachedEntry.cachedAt)) {
            val tracks = cachedEntry.dataIds.mapNotNull { id ->
                trackDao.getTrackById(id)?.toDomain()
            }
            if (tracks.size == cachedEntry.dataIds.size) return@runCatching tracks
        }

        val likedIdsRes = UserApiService.getLikedTrackIds()
        if (likedIdsRes.status != HttpStatusCode.OK) {
            return@runCatching cachedEntry?.dataIds?.mapNotNull { id ->
                trackDao.getTrackById(id)?.toDomain()
            } ?: throw Exception("Failed to get liked ids")
        }

        val tracks = likedIdsRes.value.map { id ->
            val cachedTrack = trackDao.getTrackById(id.toString())
            if (cachedTrack != null && !isCacheExpired(cachedTrack.cachedAt)) {
                cachedTrack.toDomain()
            } else {
                val trackRes = TrackApiService.getTrack(id)
                if (trackRes.status != HttpStatusCode.OK) throw Exception("Failed to get track $id")

                var coverUrl = ""
                if (trackRes.value.album != null) {
                    val albumRes = AlbumsApiService.getAlbum(trackRes.value.album.id)
                    if (albumRes.status == HttpStatusCode.OK) {
                        coverUrl = albumRes.value.coverUrl ?: ""
                    }
                }
                val domainTrack = trackRes.value.toDomain(isLiked = true, coverUrl = coverUrl)
                trackDao.insertTrack(domainTrack.toEntity())
                domainTrack
            }
        }

        cacheEntryDao.insert(CacheEntryEntity(cacheKey, likedIdsRes.value.map { it.toString() }))
        tracks
    }

    override suspend fun getUserPlaylists(): Result<List<Playlist>> = runCatching {
        val cacheKey = "user_playlists"
        val cachedEntry = cacheEntryDao.getByKey(cacheKey)

        if (cachedEntry != null && !isCacheExpired(cachedEntry.cachedAt)) {
            val playlists = cachedEntry.dataIds.mapNotNull { id ->
                playlistDao.getPlaylistById(id)?.toDomain()
            }
            if (playlists.size == cachedEntry.dataIds.size) return@runCatching playlists
        }

        val playlistIdsRes = UserApiService.getUserPlaylistIds()
        if (playlistIdsRes.status != HttpStatusCode.OK) {
            return@runCatching cachedEntry?.dataIds?.mapNotNull { id ->
                playlistDao.getPlaylistById(id)?.toDomain()
            } ?: throw Exception("Failed to get playlist ids")
        }

        val playlists = playlistIdsRes.value.map { id ->
            val cached = playlistDao.getPlaylistById(id.toString())
            if (cached != null && !isCacheExpired(cached.cachedAt)) {
                cached.toDomain()
            } else {
                val playlistRes = PlaylistApiService.getPlaylist(id)
                if (playlistRes.status != HttpStatusCode.OK) throw Exception("Failed to get playlist $id")
                val domain = playlistRes.value.toDomain()
                playlistDao.insertPlaylist(domain.toEntity())
                domain
            }
        }

        cacheEntryDao.insert(CacheEntryEntity(cacheKey, playlistIdsRes.value.map { it.toString() }))
        playlists
    }

    override suspend fun createPlaylist(title: String, description: String?): Result<Playlist> = runCatching {
        val request = CreatePlaylistRequestDto(title = title, description = description, isPublic = true)
        val result = PlaylistApiService.createPlaylist(request)

        if (result.status != HttpStatusCode.Created && result.status != HttpStatusCode.OK)
            throw Exception("Failed to create playlist: ${result.status}")

        val playlist = result.value.toDomain()
        playlistDao.insertPlaylist(playlist.toEntity())
        
        // Invalidate user playlists cache entry
        cacheEntryDao.deleteByKey("user_playlists")
        
        playlist
    }

    override suspend fun addTrackToPlaylist(playlistId: String, trackId: String): Result<Unit> = runCatching {
        val res = PlaylistApiService.addTrackToPlaylist(playlistId.toLong(), trackId.toLong())
        if (res.status != HttpStatusCode.OK && res.status != HttpStatusCode.Created)
            throw Exception("Failed to add track to playlist: ${res.status}")
        
        // Update local playlist if cached
        playlistDao.getPlaylistById(playlistId)?.let { cached ->
            val updatedTrackIds = cached.trackIds.toMutableList()
            if (!updatedTrackIds.contains(trackId)) {
                updatedTrackIds.add(trackId)
                playlistDao.insertPlaylist(cached.copy(
                    trackIds = updatedTrackIds,
                    trackCount = updatedTrackIds.size,
                    cachedAt = System.currentTimeMillis()
                ))
            }
        }
    }

    override suspend fun removeTrackFromPlaylist(playlistId: String, trackId: String): Result<Unit> = runCatching {
        val res = PlaylistApiService.removeTrackFromPlaylist(playlistId.toLong(), trackId.toLong())
        if (res.status != HttpStatusCode.OK && res.status != HttpStatusCode.NoContent)
            throw Exception("Failed to remove track from playlist: ${res.status}")

        // Update local playlist if cached
        playlistDao.getPlaylistById(playlistId)?.let { cached ->
            val updatedTrackIds = cached.trackIds.toMutableList()
            if (updatedTrackIds.remove(trackId)) {
                playlistDao.insertPlaylist(cached.copy(
                    trackIds = updatedTrackIds,
                    trackCount = updatedTrackIds.size,
                    cachedAt = System.currentTimeMillis()
                ))
            }
        }
    }

    override suspend fun getPlaylist(playlistId: String): Result<Playlist> = runCatching {
        val cached = playlistDao.getPlaylistById(playlistId)
        if (cached != null && !isCacheExpired(cached.cachedAt)) {
            return@runCatching cached.toDomain()
        }

        val playlistRes = try {
            PlaylistApiService.getPlaylist(playlistId.toLong())
        } catch (e: Exception) {
            return@runCatching cached?.toDomain() ?: throw e
        }

        if (playlistRes.status != HttpStatusCode.OK) {
            return@runCatching cached?.toDomain() ?: throw Exception("Failed to get playlist $playlistId")
        }

        val playlist = playlistRes.value.toDomain()
        playlistDao.insertPlaylist(playlist.toEntity())
        playlist
    }

    private fun isCacheExpired(cachedAt: Long): Boolean {
        return System.currentTimeMillis() - cachedAt > CACHE_EXPIRATION_TIME
    }

    companion object {
        private const val CACHE_EXPIRATION_TIME = 24 * 60 * 60 * 1000L // 24 hours
    }
}
