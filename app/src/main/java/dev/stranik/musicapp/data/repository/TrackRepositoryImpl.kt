package dev.stranik.musicapp.data.repository

import dev.stranik.musicapp.data.remote.AlbumsApiService
import dev.stranik.musicapp.data.remote.TrackApiService
import dev.stranik.musicapp.data.remote.UserApiService
import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.domain.repository.TrackRepository
import dev.stranik.musicapp.presentation.mapper.toDomain
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking

class TrackRepositoryImpl : TrackRepository {
    override suspend fun getTrack(trackId: Long): Result<Track> = runCatching {
        val res = TrackApiService.getTrack(trackId)

        if (res.status != HttpStatusCode.OK)
            throw Exception("Failed to get track: ${res.status}")

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

        res.value.toDomain(isLiked = isLiked, coverUrl = coverUrl)
    }

    override suspend fun likeTrack(trackId: Long): Result<Unit> = runCatching {
        val res = TrackApiService.likeTrack(trackId)

        if (res.status != HttpStatusCode.OK && res.status != HttpStatusCode.Created)
            throw Exception("Failed to like track: ${res.status}")
    }

    override suspend fun unlikeTrack(trackId: Long): Result<Unit> = runCatching {
        val res = TrackApiService.unlikeTrack(trackId)

        if (res.status != HttpStatusCode.OK && res.status != HttpStatusCode.NoContent)
            throw Exception("Failed to unlike track: ${res.status}")
    }

    override fun getHlsManifestUrl(trackId: Long): String {
        return TrackApiService.getHlsManifestUrl(trackId)
    }

    private fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "%d:%02d".format(minutes, remainingSeconds)
    }
}
