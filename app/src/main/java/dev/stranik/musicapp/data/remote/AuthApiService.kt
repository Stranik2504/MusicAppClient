package dev.stranik.musicapp.data.remote

import android.util.Log
import dev.stranik.musicapp.data.model.LoginRequestDto
import dev.stranik.musicapp.data.model.LoginResponseDto
import dev.stranik.musicapp.data.model.Res
import dev.stranik.musicapp.data.model.UserDto
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

object AuthApiService {
    suspend fun login(username: String, password: String): Res<LoginResponseDto> {
        val result = KtorClient.client.post("api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(username = username, password = password))
        }

        Log.d("register", result.body())
        Log.d("register", ("$username $password"))
        Log.d("register", result.toString())

        return Res(result.status, result.body())
    }

    suspend fun register(user: UserDto): Res<String> {
        val result = KtorClient.client.post("api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }

        return Res(result.status, result.body())
    }
}