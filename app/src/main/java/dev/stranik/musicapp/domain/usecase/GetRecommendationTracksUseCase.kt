package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.domain.repository.RecommendationRepository

class GetRecommendationTracksUseCase(
    private val recommendationRepository: RecommendationRepository
) {
    suspend operator fun invoke(): Result<List<Track>> {
        recommendationRepository.getHomeRecommendations().onSuccess { homeRecommendations ->
            return Result.success(homeRecommendations.tracks)
        }

        return Result.failure(Exception("Failed to fetch home recommendations"))
    }
}