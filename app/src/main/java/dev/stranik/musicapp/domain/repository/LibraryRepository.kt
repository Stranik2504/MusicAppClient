package dev.stranik.musicapp.domain.repository

import dev.stranik.musicapp.domain.model.Playlist
import dev.stranik.musicapp.domain.model.Track

interface LibraryRepository {
    suspend fun getLikedTracks(): Result<List<Track>>
    suspend fun getUserPlaylists(): Result<List<Playlist>>
    suspend fun createPlaylist(title: String, description: String?): Result<Playlist>
    suspend fun addTrackToPlaylist(playlistId: String, trackId: String): Result<Unit>
    suspend fun removeTrackFromPlaylist(playlistId: String, trackId: String): Result<Unit>
    suspend fun getPlaylist(playlistId: String): Result<Playlist>
}
