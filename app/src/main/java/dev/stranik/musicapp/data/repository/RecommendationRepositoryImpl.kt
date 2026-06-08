package dev.stranik.musicapp.data.repository

import dev.stranik.musicapp.data.local.dao.AlbumDao
import dev.stranik.musicapp.data.local.dao.ArtistDao
import dev.stranik.musicapp.data.local.dao.CacheEntryDao
import dev.stranik.musicapp.data.local.dao.TrackDao
import dev.stranik.musicapp.data.local.entity.CacheEntryEntity
import dev.stranik.musicapp.data.local.entity.toDomain
import dev.stranik.musicapp.data.local.entity.toEntity
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

class RecommendationRepositoryImpl(
    private val trackDao: TrackDao,
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao,
    private val cacheEntryDao: CacheEntryDao
) : RecommendationRepository {

    override suspend fun getTrackRecommendations(trackId: Long): Result<List<Track>> = runCatching {
        val cacheKey = "track_recommendations_$trackId"
        val cachedEntry = cacheEntryDao.getByKey(cacheKey)

        if (cachedEntry != null && !isCacheExpired(cachedEntry.cachedAt)) {
            val tracks = cachedEntry.dataIds.mapNotNull { trackDao.getTrackById(it)?.toDomain() }
            if (tracks.size == cachedEntry.dataIds.size) return@runCatching tracks
        }

        val res = RecommendationsApiService.getTrackRecommendations(trackId)
        if (res.status != HttpStatusCode.OK) {
            return@runCatching cachedEntry?.dataIds?.mapNotNull { trackDao.getTrackById(it)?.toDomain() }
                ?: throw Exception("Failed to get track recommendations")
        }

        val likedTrackIds = getLikedTrackIds()
        val tracks = res.value.tracks.map {
            val cached = trackDao.getTrackById(it.id.toString())
            if (cached != null && !isCacheExpired(cached.cachedAt)) {
                cached.toDomain()
            } else {
                var coverUrl = ""
                if (it.album != null) {
                    val albumRes = AlbumsApiService.getAlbum(it.album.id)
                    if (albumRes.status == HttpStatusCode.OK) {
                        coverUrl = albumRes.value.coverUrl ?: ""
                    }
                }
                val domain = it.toDomain(isLiked = likedTrackIds.contains(it.id), coverUrl = coverUrl)
                trackDao.insertTrack(domain.toEntity())
                domain
            }
        }

        cacheEntryDao.insert(CacheEntryEntity(cacheKey, res.value.tracks.map { it.id.toString() }))
        tracks
    }

    override suspend fun getArtistRecommendations(artistId: Long): Result<List<Track>> = runCatching {
        val cacheKey = "artist_recommendations_$artistId"
        val cachedEntry = cacheEntryDao.getByKey(cacheKey)

        if (cachedEntry != null && !isCacheExpired(cachedEntry.cachedAt)) {
            val tracks = cachedEntry.dataIds.mapNotNull { trackDao.getTrackById(it)?.toDomain() }
            if (tracks.size == cachedEntry.dataIds.size) return@runCatching tracks
        }

        val res = RecommendationsApiService.getArtistRecommendations(artistId)
        if (res.status != HttpStatusCode.OK) {
            return@runCatching cachedEntry?.dataIds?.mapNotNull { trackDao.getTrackById(it)?.toDomain() }
                ?: throw Exception("Failed to get artist recommendations")
        }

        val likedTrackIds = getLikedTrackIds()
        val tracks = res.value.tracks.map {
            val cached = trackDao.getTrackById(it.id.toString())
            if (cached != null && !isCacheExpired(cached.cachedAt)) {
                cached.toDomain()
            } else {
                val domain = it.toDomain(isLiked = likedTrackIds.contains(it.id))
                trackDao.insertTrack(domain.toEntity())
                domain
            }
        }

        cacheEntryDao.insert(CacheEntryEntity(cacheKey, res.value.tracks.map { it.id.toString() }))
        tracks
    }

    override suspend fun getHomeRecommendations(limit: Int): Result<HomeRecommendations> = runCatching {
        val cacheKeyTracks = "home_recommendations_tracks"
        val cacheKeyAlbums = "home_recommendations_albums"
        
        val cachedTracksEntry = cacheEntryDao.getByKey(cacheKeyTracks)
        val cachedAlbumsEntry = cacheEntryDao.getByKey(cacheKeyAlbums)

        if (cachedTracksEntry != null && cachedAlbumsEntry != null && 
            !isCacheExpired(cachedTracksEntry.cachedAt) && !isCacheExpired(cachedAlbumsEntry.cachedAt)) {
            val tracks = cachedTracksEntry.dataIds.mapNotNull { trackDao.getTrackById(it)?.toDomain() }
            val albums = cachedAlbumsEntry.dataIds.mapNotNull { albumDao.getAlbumById(it)?.toDomain() }
            
            if (tracks.size == cachedTracksEntry.dataIds.size && albums.size == cachedAlbumsEntry.dataIds.size) {
                return@runCatching HomeRecommendations(albums = albums, tracks = tracks)
            }
        }

        val res = try {
            RecommendationsApiService.getHomeRecommendations(limit)
        } catch (e: Exception) {
            val tracks = cachedTracksEntry?.dataIds?.mapNotNull { trackDao.getTrackById(it)?.toDomain() } ?: emptyList()
            val albums = cachedAlbumsEntry?.dataIds?.mapNotNull { albumDao.getAlbumById(it)?.toDomain() } ?: emptyList()
            if (tracks.isNotEmpty() || albums.isNotEmpty()) return@runCatching HomeRecommendations(albums, tracks)
            throw e
        }

        if (res.status != HttpStatusCode.OK) throw Exception("Failed to get home recommendations")

        val likedTrackIds = getLikedTrackIds()
        
        val albums = res.value.albums.map {
            val domain = it.toDomain()
            albumDao.insertAlbum(domain.toEntity())
            domain
        }

        val tracks = res.value.tracks.map {
            val cached = trackDao.getTrackById(it.id.toString())
            if (cached != null && !isCacheExpired(cached.cachedAt)) {
                cached.toDomain()
            } else {
                var coverUrl = ""
                if (it.album != null) {
                    val albumRes = AlbumsApiService.getAlbum(it.album.id)
                    if (albumRes.status == HttpStatusCode.OK) coverUrl = albumRes.value.coverUrl ?: ""
                }
                val domain = it.toDomain(isLiked = likedTrackIds.contains(it.id), coverUrl = coverUrl)
                trackDao.insertTrack(domain.toEntity())
                domain
            }
        }

        cacheEntryDao.insert(CacheEntryEntity(cacheKeyTracks, res.value.tracks.map { it.id.toString() }))
        cacheEntryDao.insert(CacheEntryEntity(cacheKeyAlbums, res.value.albums.map { it.id.toString() }))

        HomeRecommendations(albums = albums, tracks = tracks)
    }

    override suspend fun getRecentlyPlayed(): Result<List<Track>> = runCatching {
        val cacheKey = "recently_played"
        val cachedEntry = cacheEntryDao.getByKey(cacheKey)

        if (cachedEntry != null && !isCacheExpired(cachedEntry.cachedAt)) {
            val tracks = cachedEntry.dataIds.mapNotNull { trackDao.getTrackById(it)?.toDomain() }
            if (tracks.size == cachedEntry.dataIds.size) return@runCatching tracks
        }

        val res = UserApiService.getRecentlyPlayed()
        if (res.status != HttpStatusCode.OK) {
            return@runCatching cachedEntry?.dataIds?.mapNotNull { trackDao.getTrackById(it)?.toDomain() }
                ?: throw Exception("Failed to get recently played")
        }

        val likedTrackIds = getLikedTrackIds()
        val tracks = res.value.map { history ->
            val cached = trackDao.getTrackById(history.trackId.toString())
            if (cached != null && !isCacheExpired(cached.cachedAt)) {
                cached.toDomain()
            } else {
                val trackRes = TrackApiService.getTrack(history.trackId)
                if (trackRes.status == HttpStatusCode.OK) {
                    val domain = trackRes.value.toDomain(isLiked = likedTrackIds.contains(history.trackId))
                    trackDao.insertTrack(domain.toEntity())
                    domain
                } else {
                    throw Exception("Failed to fetch track details")
                }
            }
        }

        cacheEntryDao.insert(CacheEntryEntity(cacheKey, res.value.map { it.trackId.toString() }))
        tracks
    }

    override suspend fun getFollowedArtists(): Result<List<Artist>> = runCatching {
        val cacheKey = "followed_artists"
        val cachedEntry = cacheEntryDao.getByKey(cacheKey)

        if (cachedEntry != null && !isCacheExpired(cachedEntry.cachedAt)) {
            val artists = cachedEntry.dataIds.mapNotNull { artistDao.getArtistById(it)?.toDomain() }
            if (artists.size == cachedEntry.dataIds.size) return@runCatching artists
        }

        val res = UserApiService.getUserFollows()
        if (res.status != HttpStatusCode.OK) {
            return@runCatching cachedEntry?.dataIds?.mapNotNull { artistDao.getArtistById(it)?.toDomain() }
                ?: throw Exception("Failed to get followed artists")
        }

        val artists = res.value.map { id ->
            val cached = artistDao.getArtistById(id.toString())
            if (cached != null && !isCacheExpired(cached.cachedAt)) {
                cached.toDomain()
            } else {
                val artistRes = ArtistApiService.getArtist(id)
                if (artistRes.status == HttpStatusCode.OK) {
                    val domain = artistRes.value.toDomain()
                    artistDao.insertArtist(domain.toEntity())
                    domain
                } else {
                    throw Exception("Failed to fetch artist details")
                }
            }
        }

        cacheEntryDao.insert(CacheEntryEntity(cacheKey, res.value.map { it.toString() }))
        artists
    }

    private suspend fun getLikedTrackIds(): Set<Long> {
        val res = UserApiService.getLikedTrackIds()
        return if (res.status == HttpStatusCode.OK) res.value.toSet() else emptySet()
    }

    private fun isCacheExpired(cachedAt: Long): Boolean {
        return System.currentTimeMillis() - cachedAt > CACHE_EXPIRATION_TIME
    }

    companion object {
        private const val CACHE_EXPIRATION_TIME = 24 * 60 * 60 * 1000L // 24 hours
    }
}
