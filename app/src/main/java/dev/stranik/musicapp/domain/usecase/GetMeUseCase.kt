package dev.stranik.musicapp.domain.usecase

import dev.stranik.musicapp.domain.model.User
import dev.stranik.musicapp.domain.repository.UserRepository

class GetMeUseCase(
    val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<User> {
        return userRepository.getMe()
    }
}