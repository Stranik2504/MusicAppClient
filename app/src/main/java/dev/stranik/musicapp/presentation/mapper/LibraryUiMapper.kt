package dev.stranik.musicapp.presentation.mapper

import dev.stranik.musicapp.domain.model.Playlist
import dev.stranik.musicapp.domain.model.Track

class LibraryUiMapper {
    fun toPlaylist(playlist: Playlist): Playlist = playlist
    fun toTrack(track: Track): Track = track
}

