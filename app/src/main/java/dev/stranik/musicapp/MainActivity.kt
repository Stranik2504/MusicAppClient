package dev.stranik.musicapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import dev.stranik.musicapp.data.local.TokenManager
import dev.stranik.musicapp.data.remote.KtorClient
import dev.stranik.musicapp.navigation.MusicNavGraph
import dev.stranik.musicapp.ui.theme.MusicAppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tokenManager = TokenManager(applicationContext)

        lifecycleScope.launch {
            tokenManager.accessTokenFlow.first()?.let { token ->
                KtorClient.updateAccessToken(token)
            }
        }

        enableEdgeToEdge()
        setContent {
            MusicAppTheme {
                val navController = rememberNavController()
                MusicNavGraph(context = applicationContext, navController = navController)
            }
        }
    }
}