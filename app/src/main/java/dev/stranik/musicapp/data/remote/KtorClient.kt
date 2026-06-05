package dev.stranik.musicapp.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KtorClient {
    private var currentAccessToken: String? = null
    var client = buildClient()

    private fun buildClient() = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 15000
        }

        /*install("TokenInterceptor") {
            requestPipeline.intercept(HttpRequestPipeline.State) {
                currentAccessToken?.let { token ->
                    context.header("Authorization", "Bearer $token")
                }
            }
        }*/

        install(Auth) {
            bearer {
                loadTokens {
                    currentAccessToken?.let {
                        BearerTokens(it, "")
                    }
                }

                sendWithoutRequest { request ->
                    request.url.build().encodedPath.startsWith("/api")
                }
            }
        }

        defaultRequest {
            // url("https://musicapp.stranik.dev/")
            url("http://192.168.1.11:8080/")
        }
    }

    fun updateAccessToken(token: String) {
        currentAccessToken = token
        client = buildClient()
    }

    fun clearTokens() {
        currentAccessToken = null
        client = buildClient()
    }
}