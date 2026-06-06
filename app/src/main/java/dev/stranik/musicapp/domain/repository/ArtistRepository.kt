package dev.stranik.musicapp.domain.repository

import dev.stranik.musicapp.data.model.Res
import dev.stranik.musicapp.domain.model.Artist

interface ArtistRepository {
    suspend fun getArtist(artistId: Long): Result<Artist>
}