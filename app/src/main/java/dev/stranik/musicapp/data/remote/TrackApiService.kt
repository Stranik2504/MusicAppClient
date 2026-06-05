package dev.stranik.musicapp.data.remote

import android.util.Log
import dev.stranik.musicapp.data.model.Res
import dev.stranik.musicapp.data.model.TrackDto
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post

object TrackApiService {
    suspend fun getTrack(trackId: Long): Res<TrackDto> {
        val result = KtorClient.client.get("api/tracks/$trackId")
        return Res(result.status, result.body())
    }

    suspend fun likeTrack(trackId: Long): Res<Unit> {
        val result = KtorClient.client.post("api/tracks/$trackId/like")
        return Res(result.status, Unit)
    }

    suspend fun unlikeTrack(trackId: Long): Res<Unit> {
        val result = KtorClient.client.delete("api/tracks/$trackId/like")
        return Res(result.status, Unit)
    }

    fun getHlsManifestUrl(trackId: Long): String {
        return "${KtorClient.BASE_URL}/api/tracks/$trackId/hls"
    }
}
