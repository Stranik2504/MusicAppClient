package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.model.UserRegistration
import dev.stranik.musicapp.domain.repository.AuthRepository

class RegistrationUseCase(
    val authRepository: AuthRepository
) {
    suspend operator fun invoke(user: UserRegistration): Result<String> {
        return authRepository.register(user)
    }
}