package dev.stranik.musicapp.domain.repository

import dev.stranik.musicapp.domain.model.Track

interface TrackRepository {
    suspend fun getTrack(trackId: Long): Result<Track>
    suspend fun likeTrack(trackId: Long): Result<Unit>
    suspend fun unlikeTrack(trackId: Long): Result<Unit>
    fun getHlsManifestUrl(trackId: Long): String
}
