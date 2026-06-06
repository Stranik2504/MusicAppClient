package dev.stranik.musicapp.data.repository

import dev.stranik.musicapp.data.model.AlbumDto
import dev.stranik.musicapp.data.model.ArtistDto
import dev.stranik.musicapp.data.model.TrackDto
import dev.stranik.musicapp.data.remote.AlbumsApiService
import dev.stranik.musicapp.data.remote.SearchApiService
import dev.stranik.musicapp.data.remote.UserApiService
import dev.stranik.musicapp.domain.model.SearchResult
import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.domain.repository.SearchRepository
import dev.stranik.musicapp.presentation.mapper.toDomain
import io.ktor.http.HttpStatusCode

class SearchRepositoryImpl : SearchRepository {
    override suspend fun search(
        query: String,
        limit: Int
    ): SearchResult {
        val response = SearchApiService.search(query, limit)

        if (response.status != HttpStatusCode.OK)
            throw Exception("Failed to search: ${response.status}")

        var tracks = emptyList<TrackDto>()
        var artists = emptyList<ArtistDto>()
        var albums = emptyList<AlbumDto>()

        var trackResult = emptyList<Track>()

        if (response.value.tracks != null)
            tracks = response.value.tracks

        if (response.value.artists != null)
            artists = response.value.artists

        if (response.value.albums != null)
            albums = response.value.albums

        if (tracks.isNotEmpty()) {
            val likedTrackIds = getLikedTrackIds()

            trackResult = tracks.map {
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

        return SearchResult(
            tracks = trackResult,
            artists = artists.map { it.toDomain() },
            albums = albums.map { it.toDomain() }
        )
    }

    private suspend fun getLikedTrackIds(): Set<Long> {
        val res = UserApiService.getLikedTrackIds()
        return if (res.status == HttpStatusCode.OK) res.value.toSet() else emptySet()
    }
}