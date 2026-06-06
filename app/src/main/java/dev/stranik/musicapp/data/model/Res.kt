package dev.stranik.musicapp.data.model

import io.ktor.http.HttpStatusCode

data class Res<T>(
    val status: HttpStatusCode,
    val value: T
)