package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.domain.repository.RecommendationRepository

class GetRecentlyPlayedUseCase(
    private val recommendationRepository: RecommendationRepository
) {
    suspend operator fun invoke(): Result<List<Track>> {
        return recommendationRepository.getRecentlyPlayed()
    }
}

