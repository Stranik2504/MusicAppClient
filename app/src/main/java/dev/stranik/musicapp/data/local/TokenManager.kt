package dev.stranik.musicapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_tokens")

class TokenManager(private val context: Context) {
    private val ACCESS_TOKEN = stringPreferencesKey("access_token")

    val accessTokenFlow: Flow<String?> = context.tokenDataStore.data
        .map { prefs -> prefs[ACCESS_TOKEN] }

    suspend fun saveAccessToken(token: String) {
        context.tokenDataStore.edit {
            it[ACCESS_TOKEN] = token
        }
    }

    suspend fun clearTokens() {
        context.tokenDataStore.edit {
            it.clear()
        }
    }

    suspend fun getAccessToken(): String? {
        val prefs = context.tokenDataStore.data.map { it[ACCESS_TOKEN] }.firstOrNull()
        return prefs
    }
}