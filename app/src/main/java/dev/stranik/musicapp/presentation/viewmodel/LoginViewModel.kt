package dev.stranik.musicapp.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.logging.Logger

data class LoginUiState(
	val email: String = "",
	val password: String = "",
	val isLoading: Boolean = false,
	val error: String? = null,
	val success: Boolean = false
)

class LoginViewModel(context: Context) : ViewModel() {

	private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

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

			val storedEmail = prefs.getString("email", null)
			val storedPassword = prefs.getString("password", null)

			// Простая синхронная проверка
			if (storedEmail == null || storedPassword == null) {
				_uiState.value = _uiState.value.copy(
					isLoading = false,
					error = "Пользователь не зарегистрирован"
				)
				return@launch
			}

			val a = _uiState.value.email
			val b = _uiState.value.password

			Log.i("LoginViewModel", "Stored email: $storedEmail, Stored password: $storedPassword")
			Log.i("LoginViewModel", "Stored email: $a, Stored password: $b")

			if (storedEmail == _uiState.value.email && storedPassword == _uiState.value.password) {
				prefs.edit().putBoolean("is_logged", true).apply()
				_uiState.value = _uiState.value.copy(isLoading = false, success = true)
			} else {
				_uiState.value = _uiState.value.copy(isLoading = false, error = "Неверный email или пароль")
			}
		}
	}

	companion object {
		fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
			initializer {
				LoginViewModel(context)
			}
		}
	}
}