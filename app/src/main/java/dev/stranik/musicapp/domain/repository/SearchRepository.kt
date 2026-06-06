package dev.stranik.musicapp.domain.repository

import dev.stranik.musicapp.domain.model.SearchResult

interface SearchRepository {
    suspend fun search(query: String, limit: Int = 20): SearchResult
}