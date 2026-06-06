package dev.stranik.musicapp.domain.repository

import dev.stranik.musicapp.domain.model.Artist
import dev.stranik.musicapp.domain.model.HomeRecommendations
import dev.stranik.musicapp.domain.model.Track

interface RecommendationRepository {
    suspend fun getTrackRecommendations(trackId: Long): Result<List<Track>>
    suspend fun getArtistRecommendations(artistId: Long): Result<List<Track>>
    suspend fun getHomeRecommendations(limit: Int = 25): Result<HomeRecommendations>
    suspend fun getRecentlyPlayed(): Result<List<Track>>
    suspend fun getFollowedArtists(): Result<List<Artist>>
}
