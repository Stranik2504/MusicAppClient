package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.repository.PlayerRepository
import dev.stranik.musicapp.domain.repository.TrackRepository

class PlayTrackUseCase(
    private val playerRepository: PlayerRepository,
    private val trackRepository: TrackRepository
) {
    suspend operator fun invoke(trackId: String) {
        val id = trackId.toLongOrNull() ?: return
        val trackResult = trackRepository.getTrack(id)
        
        trackResult.onSuccess { track ->
            val hlsUrl = trackRepository.getHlsManifestUrl(id)
            playerRepository.playTrack(track, hlsUrl)
        }
    }
    
    suspend fun resume() {
        playerRepository.resume()
    }
}
