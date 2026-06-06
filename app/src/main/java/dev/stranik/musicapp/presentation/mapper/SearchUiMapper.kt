package dev.stranik.musicapp.presentation.mapper

import dev.stranik.musicapp.data.model.ArtistDto
import dev.stranik.musicapp.domain.model.Album
import dev.stranik.musicapp.domain.model.Artist
import dev.stranik.musicapp.domain.model.Track

fun ArtistDto.toDomain() = Artist(
    id = id.toString(),
    name = name,
    avatarUrl = avatarUrl,
    topTracks = null,
    monthlyListenersFormatted = formatMonthlyListeners(monthlyListeners)
)

