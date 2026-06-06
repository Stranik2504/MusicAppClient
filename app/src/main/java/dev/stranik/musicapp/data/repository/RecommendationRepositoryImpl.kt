package dev.stranik.musicapp.data.repository

import dev.stranik.musicapp.data.remote.AlbumsApiService
import dev.stranik.musicapp.data.remote.ArtistApiService
import dev.stranik.musicapp.data.remote.RecommendationsApiService
import dev.stranik.musicapp.data.remote.TrackApiService
import dev.stranik.musicapp.data.remote.UserApiService
import dev.stranik.musicapp.domain.model.Artist
import dev.stranik.musicapp.domain.model.HomeRecommendations
import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.domain.repository.RecommendationRepository
import dev.stranik.musicapp.presentation.mapper.toDomain
import io.ktor.http.HttpStatusCode

class RecommendationRepositoryImpl : RecommendationRepository {
    override suspend fun getTrackRecommendations(trackId: Long): Result<List<Track>> = runCatching {
        val res = RecommendationsApiService.getTrackRecommendations(trackId)
        if (res.status != HttpStatusCode.OK) throw Exception("Failed to get track recommendations")
        
        val likedTrackIds = getLikedTrackIds()
        res.value.tracks.map {
            var coverUrl = ""

            if (it.album != null) {
                val albumRes = AlbumsApiService.getAlbum(it.album.id)

                if (albumRes.status != HttpStatusCode.OK)
                    throw Exception("Failed to get album ${it.album.id}")

                coverUrl = albumRes.value.coverUrl ?: ""
            }

            it.toDomain(isLiked = likedTrackIds.contains(it.id), coverUrl = coverUrl)
        }
    }

    override suspend fun getArtistRecommendations(artistId: Long): Result<List<Track>> = runCatching {
        val res = RecommendationsApiService.getArtistRecommendations(artistId)
        if (res.status != HttpStatusCode.OK) throw Exception("Failed to get artist recommendations")
        
        val likedTrackIds = getLikedTrackIds()
        res.value.tracks.map { it.toDomain(isLiked = likedTrackIds.contains(it.id)) }
    }

    override suspend fun getHomeRecommendations(limit: Int): Result<HomeRecommendations> = runCatching {
        val res = RecommendationsApiService.getHomeRecommendations(limit)
        if (res.status != HttpStatusCode.OK) throw Exception("Failed to get home recommendations")
        
        val likedTrackIds = getLikedTrackIds()
        HomeRecommendations(
            albums = res.value.albums.map { it.toDomain() },
            tracks = res.value.tracks.map {
                var coverUrl = ""

                if (it.album != null) {
                    val albumRes = AlbumsApiService.getAlbum(it.album.id)

                    if (albumRes.status != HttpStatusCode.OK)
                        throw Exception("Failed to get album ${it.album.id}")

                    coverUrl = albumRes.value.coverUrl ?: ""
                }

                it.toDomain(isLiked = likedTrackIds.contains(it.id), coverUrl = coverUrl)
            }
        )
    }

    override suspend fun getRecentlyPlayed(): Result<List<Track>> = runCatching {
        val res = UserApiService.getRecentlyPlayed()
        if (res.status != HttpStatusCode.OK) throw Exception("Failed to get recently played")
        
        val likedTrackIds = getLikedTrackIds()
        
        res.value.map { history ->
            val trackRes = TrackApiService.getTrack(history.trackId)
            if (trackRes.status == HttpStatusCode.OK) {
                 trackRes.value.toDomain(isLiked = likedTrackIds.contains(history.trackId))
            } else {
                throw Exception("Failed to fetch track details for recently played")
            }
        }
    }

    override suspend fun getFollowedArtists(): Result<List<Artist>> = runCatching {
        val res = UserApiService.getUserFollows()
        if (res.status != HttpStatusCode.OK) throw Exception("Failed to get followed artists")

        res.value.map {
            val artistRes = ArtistApiService.getArtist(it)

            if (artistRes.status == HttpStatusCode.OK) {
                artistRes.value.toDomain()
            } else {
                throw Exception("Failed to fetch artist details for followed artists")
            }
        }
    }

    private suspend fun getLikedTrackIds(): Set<Long> {
        val res = UserApiService.getLikedTrackIds()
        return if (res.status == HttpStatusCode.OK) res.value.toSet() else emptySet()
    }
}
