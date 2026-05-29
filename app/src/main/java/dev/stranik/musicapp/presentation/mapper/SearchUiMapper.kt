package dev.stranik.musicapp.presentation.mapper

import dev.stranik.musicapp.domain.model.Album
import dev.stranik.musicapp.domain.model.Artist
import dev.stranik.musicapp.domain.model.Track

class SearchUiMapper {
    fun toTrack(track: Track): Track = track
    fun toArtist(artist: Artist): Artist = artist
    fun toAlbum(album: Album): Album = album
}

