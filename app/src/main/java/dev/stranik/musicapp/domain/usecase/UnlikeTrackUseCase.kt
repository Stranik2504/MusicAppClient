package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.repository.TrackRepository

class UnlikeTrackUseCase(
    private val trackRepository: TrackRepository
) {
    suspend operator fun invoke(trackId: Long): Result<Unit> {
        return trackRepository.unlikeTrack(trackId)
    }
}
