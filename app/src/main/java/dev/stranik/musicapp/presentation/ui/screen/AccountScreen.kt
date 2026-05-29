package dev.stranik.musicapp.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.stranik.musicapp.presentation.viewmodel.AccountViewModel

@Composable
fun AccountScreen(
	viewModel: AccountViewModel,
	onLogout: () -> Unit,
	onEdit: () -> Unit
) {
	val state by viewModel.uiState.collectAsState()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.Top,
		horizontalAlignment = Alignment.Start
	) {
		Spacer(modifier = Modifier.height(24.dp))
		Text(text = "Аккаунт", style = MaterialTheme.typography.headlineMedium)

		Spacer(modifier = Modifier.height(16.dp))
		Text(text = "Email: ${state.email}")

		Spacer(modifier = Modifier.height(24.dp))
		Button(onClick = onEdit) { Text("Редактировать") }

		Spacer(modifier = Modifier.height(12.dp))
		Button(onClick = {
			viewModel.logout()
			onLogout()
		}) { Text("Выйти") }
	}
}
