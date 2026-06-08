package dev.stranik.musicapp.presentation.ui.screen

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import dev.stranik.musicapp.R
import dev.stranik.musicapp.presentation.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
	viewModel: LoginViewModel,
	onLoginSuccess: () -> Unit,
	onNavigateToRegistration: () -> Unit
) {
	val state by viewModel.uiState.collectAsState()

	Box(modifier = Modifier.fillMaxSize()) {
		TextButton(
			onClick = {
				val currentLocale = AppCompatDelegate.getApplicationLocales().toLanguageTags()
				val newLocale = if (currentLocale.startsWith("ru")) "en" else "ru"
				AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(newLocale))
			},
			modifier = Modifier
				.align(Alignment.TopEnd)
				.padding(8.dp)
		) {
			val currentLocale = AppCompatDelegate.getApplicationLocales().toLanguageTags()
			Text(
				text = if (currentLocale.startsWith("ru")) "RU" else "EN",
				style = MaterialTheme.typography.labelLarge
			)
		}

		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(16.dp),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				text = stringResource(R.string.login_title),
				style = MaterialTheme.typography.headlineMedium
			)

			Spacer(modifier = Modifier.height(16.dp))

			OutlinedTextField(
				value = state.email,
				onValueChange = viewModel::onEmailChange,
				label = { Text(stringResource(R.string.email_label)) }
			)

			Spacer(modifier = Modifier.height(8.dp))

			OutlinedTextField(
				value = state.password,
				onValueChange = viewModel::onPasswordChange,
				label = { Text(stringResource(R.string.password_label)) }
			)

			Spacer(modifier = Modifier.height(16.dp))

			if (state.error != null) {
				Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
				Spacer(modifier = Modifier.height(8.dp))
			}

			Button(onClick = { viewModel.login() }) {
				Text(stringResource(R.string.login_button))
			}

			Spacer(modifier = Modifier.height(8.dp))
			Button(onClick = onNavigateToRegistration) {
				Text(stringResource(R.string.registration_button))
			}
		}
	}

	LaunchedEffect(state.success) {
		if (state.success) {
			onLoginSuccess()
		}
	}
}
