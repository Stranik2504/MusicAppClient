package dev.stranik.musicapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.stranik.musicapp.domain.Creator
import dev.stranik.musicapp.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
	val email: String = "",
	val password: String = "",
	val isLoading: Boolean = false,
	val error: String? = null,
	val success: Boolean = false
)

class LoginViewModel(
	private val loginUseCase: LoginUseCase
) : ViewModel() {
	private val _uiState = MutableStateFlow(LoginUiState())
	val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

	fun onEmailChange(email: String) {
		_uiState.value = _uiState.value.copy(email = email, error = null)
	}

	fun onPasswordChange(password: String) {
		_uiState.value = _uiState.value.copy(password = password, error = null)
	}

	fun login() {
		viewModelScope.launch {
			_uiState.value = _uiState.value.copy(isLoading = true, error = null, success = false)

			val result = loginUseCase(_uiState.value.email, _uiState.value.password)

			if (result.isFailure) {
				_uiState.value = _uiState.value.copy(isLoading = false, error = "Ошибка при входе: ${result.exceptionOrNull()?.message}")
				return@launch
			}

			_uiState.value = _uiState.value.copy(isLoading = false, success = true)
		}
	}

	companion object {
		fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
			initializer {
				val tokenManager = Creator.provideTokenManager(context)
				val authRepository = Creator.provideAuthRepository(tokenManager)
				val login = Creator.provideLoginUseCase(authRepository)

				LoginViewModel(login)
			}
		}
	}
}