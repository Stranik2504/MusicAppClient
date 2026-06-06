package dev.stranik.musicapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dev.stranik.musicapp.domain.Creator
import dev.stranik.musicapp.domain.model.UserRegistration
import dev.stranik.musicapp.domain.usecase.RegistrationUseCase

data class RegistrationUiState(
	val username: String = "",
	val email: String = "",
	val password: String = "",
	val confirmPassword: String = "",
	val isLoading: Boolean = false,
	val error: String? = null,
	val success: Boolean = false
)

class RegistrationViewModel(
	private val registration: RegistrationUseCase
) : ViewModel() {
	private val _uiState = MutableStateFlow(RegistrationUiState())
	val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

	fun onUsernameChange(username: String) {
		_uiState.value = _uiState.value.copy(username = username, error = null)
	}

	fun onEmailChange(email: String) {
		_uiState.value = _uiState.value.copy(email = email, error = null)
	}

	fun onPasswordChange(password: String) {
		_uiState.value = _uiState.value.copy(password = password, error = null)
	}

	fun onConfirmPasswordChange(confirm: String) {
		_uiState.value = _uiState.value.copy(confirmPassword = confirm, error = null)
	}

	fun register() {
		viewModelScope.launch {
			_uiState.value = _uiState.value.copy(isLoading = true, error = null, success = false)

			val state = _uiState.value

			if (state.email.isBlank() || state.password.isBlank() || state.confirmPassword.isBlank()) {
				_uiState.value = state.copy(isLoading = false, error = "Заполните все поля")
				return@launch
			}

			if (state.password != state.confirmPassword) {
				_uiState.value = state.copy(isLoading = false, error = "Пароли не совпадают")
				return@launch
			}

			val result = registration(
				UserRegistration(
					username = state.username,
					email = state.email,
					password = state.password
				)
			)

			if (result.isFailure) {
				_uiState.value = state.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Ошибка регистрации")
				return@launch
			}

			_uiState.value = state.copy(isLoading = false, success = true)
		}
	}

	companion object {
		fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
			initializer {
				val tokenManager = Creator.provideTokenManager(context)
				val authRepository = Creator.provideAuthRepository(tokenManager)
				val registration = Creator.provideRegistrationUseCase(authRepository)

				RegistrationViewModel(registration)
			}
		}
	}
}