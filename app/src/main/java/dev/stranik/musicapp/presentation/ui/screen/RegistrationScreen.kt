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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.stranik.musicapp.R
import dev.stranik.musicapp.presentation.viewmodel.RegistrationViewModel

@Composable
fun RegistrationScreen(
	viewModel: RegistrationViewModel,
	onRegistrationSuccess: () -> Unit
) {
	val state by viewModel.uiState.collectAsState()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = stringResource(R.string.registration_title), 
			style = MaterialTheme.typography.headlineMedium
		)

		Spacer(modifier = Modifier.height(16.dp))

		OutlinedTextField(
			value = state.username,
			onValueChange = viewModel::onUsernameChange,
			label = { Text(stringResource(R.string.username_label)) }
		)

		Spacer(modifier = Modifier.height(8.dp))

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

		Spacer(modifier = Modifier.height(8.dp))

		OutlinedTextField(
			value = state.confirmPassword,
			onValueChange = viewModel::onConfirmPasswordChange,
			label = { Text(stringResource(R.string.confirm_password_label)) }
		)

		Spacer(modifier = Modifier.height(16.dp))
		if (state.error != null) {
			Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
			Spacer(modifier = Modifier.height(8.dp))
		}

		Button(onClick = { viewModel.register() }) { 
			Text(stringResource(R.string.register_button)) 
		}
	}

	LaunchedEffect(state.success) {
		if (state.success) {
			onRegistrationSuccess()
		}
	}
}
