package dev.stranik.musicapp.data.remote

import dev.stranik.musicapp.data.model.Res
import dev.stranik.musicapp.data.model.SearchResultDto
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType

object SearchApiService {
    suspend fun search(query: String, limit: Int): Res<SearchResultDto> {
        val result = KtorClient.client.get("api/search?q=$query&limit=$limit") {
            contentType(ContentType.Application.Json)
        }

        return Res(result.status, result.body())
    }
}