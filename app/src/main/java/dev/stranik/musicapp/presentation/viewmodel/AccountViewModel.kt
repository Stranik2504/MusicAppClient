package dev.stranik.musicapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.stranik.musicapp.domain.Creator
import dev.stranik.musicapp.domain.model.User
import dev.stranik.musicapp.domain.usecase.GetMeUseCase
import dev.stranik.musicapp.domain.usecase.IsLoggedInUseCase
import dev.stranik.musicapp.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

data class AccountUiState(
	val username: String = "",
	val email: String = "",
	val avatarUrl: String? = null,
	val isLogged: Boolean = false
)

class AccountViewModel(
	private val logoutUseCase: LogoutUseCase,
	private val isLoggedIn: IsLoggedInUseCase,
	private val getMe: GetMeUseCase
) : ViewModel() {
	private val _uiState = MutableStateFlow(AccountUiState())
	val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

	init {
		loadAccount()
	}

	fun loadAccount() {
		viewModelScope.launch {
			val me = getMe()
			val isLogged = isLoggedIn()

			if (me.isFailure) {
				_uiState.value = AccountUiState(isLogged = false)
				return@launch
			}

			val user = me.getOrDefault(User("", "", null))

			_uiState.value = AccountUiState(
				username = user.username,
				email = user.email,
				avatarUrl = user.avatarUrl,
				isLogged = isLogged
			)
		}
	}

	fun logout() {
		runBlocking {
			logoutUseCase()
		}
		_uiState.value = _uiState.value.copy(isLogged = false)
	}

	companion object {
		fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
			initializer {
				val tokenManager = Creator.provideTokenManager(context)
				val authRepository = Creator.provideAuthRepository(tokenManager)
				val userRepository = Creator.provideUserRepository(context)
				val logout = Creator.provideLogoutUseCase(authRepository)
				val isLoggedIn = Creator.provideIsLoggedInUseCase(authRepository)
				val getMe = Creator.provideGetMe(userRepository)

				AccountViewModel(
					logout,
					isLoggedIn,
					getMe
				)
			}
		}
	}
}