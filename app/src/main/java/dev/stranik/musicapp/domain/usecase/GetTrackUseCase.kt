package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.domain.repository.TrackRepository

class GetTrackUseCase(
    private val trackRepository: TrackRepository
) {
    suspend operator fun invoke(trackId: Long): Result<Track> {
        return trackRepository.getTrack(trackId)
    }
}