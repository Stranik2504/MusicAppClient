package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.model.Artist
import dev.stranik.musicapp.domain.repository.RecommendationRepository

class GetPopularArtistsUseCase(
    private val recommendationRepository: RecommendationRepository
) {
    suspend operator fun invoke(): Result<List<Artist>> {
        return recommendationRepository.getFollowedArtists()
    }
}

