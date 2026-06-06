package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.model.Album
import dev.stranik.musicapp.domain.repository.RecommendationRepository

class GetFeaturedAlbumsUseCase(
    private val recommendationRepository: RecommendationRepository
) {
    suspend operator fun invoke(): Result<List<Album>> {
        recommendationRepository.getHomeRecommendations().onSuccess { homeRecommendations ->
            return Result.success(homeRecommendations.albums)
        }

        return Result.failure(Exception("Failed to fetch home recommendations"))
    }
}

