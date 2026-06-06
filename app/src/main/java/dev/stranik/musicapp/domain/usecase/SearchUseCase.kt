package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.model.SearchResult
import dev.stranik.musicapp.domain.repository.SearchRepository

class SearchUseCase(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(query: String): SearchResult {
        return searchRepository.search(query)
    }
}

