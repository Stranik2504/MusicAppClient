package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.domain.repository.PlayerRepository
import dev.stranik.musicapp.domain.repository.TrackRepository

class PlayPlaylistUseCase(
    private val playerRepository: PlayerRepository,
    private val trackRepository: TrackRepository
) {
    suspend operator fun invoke(tracks: List<Track>, initialTrack: Track) {
        val initialIndex = tracks.indexOf(initialTrack).coerceAtLeast(0)
        val hlsUrls = tracks.map { track ->
            trackRepository.getHlsManifestUrl(track.id.toLong())
        }
        playerRepository.playTracks(tracks, initialIndex, hlsUrls)
    }
}
