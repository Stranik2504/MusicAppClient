package dev.stranik.musicapp.data.remote

import dev.stranik.musicapp.data.model.HomeRecommendationsDto
import dev.stranik.musicapp.data.model.Res
import dev.stranik.musicapp.data.model.TrackRecommendationsDto
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

object RecommendationsApiService {
    suspend fun getTrackRecommendations(trackId: Long): Res<TrackRecommendationsDto> {
        val result = KtorClient.client.get("api/recommendations/for-track/$trackId")
        return Res(result.status, result.body())
    }

    suspend fun getArtistRecommendations(artistId: Long): Res<TrackRecommendationsDto> {
        val result = KtorClient.client.get("api/recommendations/for-artist/$artistId")
        return Res(result.status, result.body())
    }

    suspend fun getHomeRecommendations(limit: Int = 25): Res<HomeRecommendationsDto> {
        val result = KtorClient.client.get("api/recommendations/home") {
            parameter("limit", limit)
        }
        return Res(result.status, result.body())
    }
}
