package dev.stranik.musicapp.data.repository

import android.util.Log
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

class LibraryRepositoryImpl : LibraryRepository {

    override suspend fun getLikedTracks(): Result<List<Track>> = runCatching {
        val likedIdsRes = UserApiService.getLikedTrackIds()

        if (likedIdsRes.status != HttpStatusCode.OK)
            throw Exception("Failed to get liked ids")

        likedIdsRes.value.map { id ->
            val trackRes = TrackApiService.getTrack(id)

            if (trackRes.status != HttpStatusCode.OK)
                throw Exception("Failed to get track $id")

            var coverUrl = ""

            if (trackRes.value.album != null) {
                val albumRes = AlbumsApiService.getAlbum(trackRes.value.album.id)

                if (albumRes.status != HttpStatusCode.OK)
                    throw Exception("Failed to get album ${trackRes.value.album.id}")

                coverUrl = albumRes.value.coverUrl ?: ""
            }

            trackRes.value.toDomain(isLiked = true, coverUrl = coverUrl)
        }
    }

    override suspend fun getUserPlaylists(): Result<List<Playlist>> = runCatching {
        val playlistIdsRes = UserApiService.getUserPlaylistIds()

        if (playlistIdsRes.status != HttpStatusCode.OK)
            throw Exception("Failed to get playlist ids")

        playlistIdsRes.value.map { id ->
            val playlistRes = PlaylistApiService.getPlaylist(id)

            if (playlistRes.status != HttpStatusCode.OK)
                throw Exception("Failed to get playlist $id")

            playlistRes.value.toDomain()
        }
    }

    override suspend fun createPlaylist(title: String, description: String?): Result<Playlist> = runCatching {
        val request = CreatePlaylistRequestDto(title = title, description = description, isPublic = true)
        val result = PlaylistApiService.createPlaylist(request)

        if (result.status != HttpStatusCode.Created && result.status != HttpStatusCode.OK)
            throw Exception("Failed to create playlist: ${result.status}")

        result.value.toDomain()
    }

    override suspend fun addTrackToPlaylist(playlistId: String, trackId: String): Result<Unit> = runCatching {
        val res = PlaylistApiService.addTrackToPlaylist(playlistId.toLong(), trackId.toLong())
        if (res.status != HttpStatusCode.OK && res.status != HttpStatusCode.Created)
            throw Exception("Failed to add track to playlist: ${res.status}")
    }

    override suspend fun removeTrackFromPlaylist(playlistId: String, trackId: String): Result<Unit> = runCatching {
        val res = PlaylistApiService.removeTrackFromPlaylist(playlistId.toLong(), trackId.toLong())
        if (res.status != HttpStatusCode.OK && res.status != HttpStatusCode.NoContent)
            throw Exception("Failed to remove track from playlist: ${res.status}")
    }

    override suspend fun getPlaylist(playlistId: String): Result<Playlist> = runCatching {
        val playlistRes = PlaylistApiService.getPlaylist(playlistId.toLong())
        if (playlistRes.status != HttpStatusCode.OK)
            throw Exception("Failed to get playlist $playlistId")
        playlistRes.value.toDomain()
    }
}
