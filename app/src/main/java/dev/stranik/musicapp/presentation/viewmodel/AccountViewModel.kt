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

data class AccountUiState(
	val username: String = "",
	val email: String = "",
	val isLogged: Boolean = false
)

class AccountViewModel(context: Context) : ViewModel() {

	private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

	private val _uiState = MutableStateFlow(AccountUiState())
	val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

	init {
		loadAccount()
	}

	fun loadAccount() {
		viewModelScope.launch {
			val email = prefs.getString("email", "") ?: ""
			val username = prefs.getString("username", "") ?: ""
			val isLogged = prefs.getBoolean("is_logged", false)
			_uiState.value = AccountUiState(username = username, email = email, isLogged = isLogged)
		}
	}

	fun logout() {
		prefs.edit().putBoolean("is_logged", false).apply()
		_uiState.value = _uiState.value.copy(isLogged = false)
	}

	companion object {
		fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
			initializer { AccountViewModel(context) }
		}
	}
}