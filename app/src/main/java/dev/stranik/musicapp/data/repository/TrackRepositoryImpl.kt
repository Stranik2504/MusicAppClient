package dev.stranik.musicapp.data.repository

import dev.stranik.musicapp.data.local.dao.TrackDao
import dev.stranik.musicapp.data.local.entity.toDomain
import dev.stranik.musicapp.data.local.entity.toEntity
import dev.stranik.musicapp.data.remote.AlbumsApiService
import dev.stranik.musicapp.data.remote.TrackApiService
import dev.stranik.musicapp.data.remote.UserApiService
import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.domain.repository.TrackRepository
import dev.stranik.musicapp.presentation.mapper.toDomain
import io.ktor.http.HttpStatusCode

class TrackRepositoryImpl(
    private val trackDao: TrackDao
) : TrackRepository {
    override suspend fun getTrack(trackId: Long): Result<Track> = runCatching {
        val cachedTrack = trackDao.getTrackById(trackId.toString())
        
        if (cachedTrack != null && !isCacheExpired(cachedTrack.cachedAt)) {
            return@runCatching cachedTrack.toDomain()
        }

        val res = TrackApiService.getTrack(trackId)

        if (res.status != HttpStatusCode.OK) {
            return@runCatching cachedTrack?.toDomain()
                ?: throw Exception("Failed to get track and no cache available: ${res.status}")
        }

        var coverUrl = ""

        if (res.value.album != null) {
            val albumRes = AlbumsApiService.getAlbum(res.value.album.id)

            if (albumRes.status != HttpStatusCode.OK)
                throw Exception("Failed to get album ${res.value.album.id}")

            coverUrl = albumRes.value.coverUrl ?: ""
        }

        val likes = UserApiService.getLikedTrackIds()
        var isLiked = false

        if (likes.status == HttpStatusCode.OK)
            isLiked = likes.value.contains(trackId)

        val track = res.value.toDomain(isLiked = isLiked, coverUrl = coverUrl)
        
        trackDao.insertTrack(track.toEntity())
        
        track
    }

    override suspend fun likeTrack(trackId: Long): Result<Unit> = runCatching {
        val res = TrackApiService.likeTrack(trackId)

        if (res.status != HttpStatusCode.OK && res.status != HttpStatusCode.Created)
            throw Exception("Failed to like track: ${res.status}")
            
        trackDao.getTrackById(trackId.toString())?.let {
            trackDao.insertTrack(it.copy(isLiked = true))
        }
    }

    override suspend fun unlikeTrack(trackId: Long): Result<Unit> = runCatching {
        val res = TrackApiService.unlikeTrack(trackId)

        if (res.status != HttpStatusCode.OK && res.status != HttpStatusCode.NoContent)
            throw Exception("Failed to unlike track: ${res.status}")

        trackDao.getTrackById(trackId.toString())?.let {
            trackDao.insertTrack(it.copy(isLiked = false))
        }
    }

    override fun getHlsManifestUrl(trackId: Long): String {
        return TrackApiService.getHlsManifestUrl(trackId)
    }

    private fun isCacheExpired(cachedAt: Long): Boolean {
        return System.currentTimeMillis() - cachedAt > CACHE_EXPIRATION_TIME
    }

    companion object {
        private const val CACHE_EXPIRATION_TIME = 24 * 60 * 60 * 1000L // 24 hours
    }
}
