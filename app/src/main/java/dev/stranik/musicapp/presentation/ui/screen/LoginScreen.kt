package dev.stranik.musicapp.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.stranik.musicapp.presentation.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
	viewModel: LoginViewModel,
	onLoginSuccess: () -> Unit,
	onNavigateToRegistration: () -> Unit
) {
	val state by viewModel.uiState.collectAsState()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(text = "Войти", style = MaterialTheme.typography.headlineMedium)

		Spacer(modifier = Modifier.height(16.dp))

		OutlinedTextField(
			value = state.email,
			onValueChange = viewModel::onEmailChange,
			label = { Text("Email") }
		)

		Spacer(modifier = Modifier.height(8.dp))

		OutlinedTextField(
			value = state.password,
			onValueChange = viewModel::onPasswordChange,
			label = { Text("Пароль") }
		)

		Spacer(modifier = Modifier.height(16.dp))

		if (state.error != null) {
			Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
			Spacer(modifier = Modifier.height(8.dp))
		}

		Button(onClick = { viewModel.login() }) { Text("Войти") }

		Spacer(modifier = Modifier.height(8.dp))
		Button(onClick = onNavigateToRegistration) { Text("Регистрация") }
	}

	LaunchedEffect(state.success) {
		if (state.success) {
			onLoginSuccess()
		}
	}
}

