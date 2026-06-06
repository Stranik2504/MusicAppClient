package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.repository.AuthRepository

class LoginUseCase(
    val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<String> {
        return authRepository.login(username, password)
    }
}