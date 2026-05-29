package dev.stranik.musicapp.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
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
		// Avatar: круглая иконка с первой буквой username (или '?' если нет)
		Box(
			modifier = Modifier
				.size(96.dp)
				.align(Alignment.CenterHorizontally)
				.clip(CircleShape)
				.background(MaterialTheme.colorScheme.primary),
			contentAlignment = Alignment.Center
		) {
			val avatarChar = state.username?.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
			Text(
				text = avatarChar,
				style = MaterialTheme.typography.headlineMedium,
				color = MaterialTheme.colorScheme.onPrimary,
				textAlign = TextAlign.Center
			)
		}

		Spacer(modifier = Modifier.height(24.dp))
		// Заголовок по центру
		Text(
			text = "Аккаунт",
			style = MaterialTheme.typography.headlineMedium,
			modifier = Modifier.align(Alignment.CenterHorizontally)
		)

		Spacer(modifier = Modifier.height(16.dp))
		Text(text = "Username: ${state.username}", modifier = Modifier.align(Alignment.CenterHorizontally))

		Spacer(modifier = Modifier.height(16.dp))
		Text(text = "Email: ${state.email}", modifier = Modifier.align(Alignment.CenterHorizontally))

		Spacer(modifier = Modifier.height(24.dp))
		// Кнопки по центру
		Button(
			onClick = onEdit,
			modifier = Modifier.align(Alignment.CenterHorizontally)
		) { Text("Редактировать") }

		Spacer(modifier = Modifier.height(12.dp))
		Button(
			onClick = {
				viewModel.logout()
				onLogout()
			},
			modifier = Modifier.align(Alignment.CenterHorizontally)
		) { Text("Выйти") }
	}
}
