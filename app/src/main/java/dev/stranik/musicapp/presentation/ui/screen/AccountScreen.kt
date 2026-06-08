package dev.stranik.musicapp.presentation.ui.screen

import androidx.appcompat.app.AppCompatDelegate
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import coil.compose.AsyncImage
import dev.stranik.musicapp.R
import dev.stranik.musicapp.presentation.viewmodel.AccountUiState
import dev.stranik.musicapp.presentation.viewmodel.AccountViewModel

@Composable
fun AccountScreen(
	viewModel: AccountViewModel,
	onLogout: () -> Unit,
	onEdit: () -> Unit
) {
	val state by viewModel.uiState.collectAsState()

	LaunchedEffect(Unit) {
		viewModel.loadAccount()
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.Top,
		horizontalAlignment = Alignment.Start
	) {
		Box(
			modifier = Modifier
				.size(96.dp)
				.align(Alignment.CenterHorizontally)
				.clip(CircleShape)
				.background(MaterialTheme.colorScheme.primary),
			contentAlignment = Alignment.Center
		) {
			MakeAvatar(state)
		}

		Spacer(modifier = Modifier.height(24.dp))

		Text(
			text = if (state.isLogged) stringResource(R.string.account_title) else stringResource(R.string.not_authorized_status),
			style = MaterialTheme.typography.headlineMedium,
			modifier = Modifier.align(Alignment.CenterHorizontally)
		)

		Spacer(modifier = Modifier.height(16.dp))
		Text(
			text = stringResource(R.string.username_format, state.username),
			modifier = Modifier.align(Alignment.CenterHorizontally)
		)

		Spacer(modifier = Modifier.height(16.dp))
		Text(
			text = stringResource(R.string.email_format, state.email),
			modifier = Modifier.align(Alignment.CenterHorizontally)
		)

		Spacer(modifier = Modifier.height(24.dp))

		/*Button(
			onClick = onEdit,
			modifier = Modifier.align(Alignment.CenterHorizontally)
		) { Text(stringResource(R.string.edit_button)) }

		Spacer(modifier = Modifier.height(12.dp))*/

		Button(
			onClick = {
				val currentLocale = AppCompatDelegate.getApplicationLocales().toLanguageTags()
				val newLocale = if (currentLocale.startsWith("ru")) "en" else "ru"
				AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(newLocale))
			},
			modifier = Modifier.align(Alignment.CenterHorizontally)
		) { Text(stringResource(R.string.change_language)) }

		Spacer(modifier = Modifier.height(12.dp))

		Button(
			onClick = {
				viewModel.logout()
				onLogout()
			},
			modifier = Modifier.align(Alignment.CenterHorizontally)
		) { Text(stringResource(R.string.logout_button)) }
	}
}

@Composable
fun MakeAvatar(state: AccountUiState) {
	val avatarChar = state.username.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

	if (state.avatarUrl == null) {
		Text(
			text = avatarChar,
			style = MaterialTheme.typography.headlineMedium,
			color = MaterialTheme.colorScheme.onPrimary,
			textAlign = TextAlign.Center
		)

		return
	}

	AsyncImage(
		model = state.avatarUrl,
		contentDescription = avatarChar,
		contentScale = ContentScale.Crop,
		modifier = Modifier.fillMaxSize()
	)
}
