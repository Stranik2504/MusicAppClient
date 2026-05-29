package dev.stranik.musicapp.navigation

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import dev.stranik.musicapp.presentation.ui.component.MiniPlayer
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import dev.stranik.musicapp.presentation.ui.screen.HomeScreen
import dev.stranik.musicapp.presentation.ui.screen.LibraryScreen
import dev.stranik.musicapp.presentation.ui.screen.PlayerScreen
import dev.stranik.musicapp.presentation.ui.screen.SearchScreen
import dev.stranik.musicapp.presentation.viewmodel.HomeViewModel
import dev.stranik.musicapp.presentation.viewmodel.LibraryViewModel
import dev.stranik.musicapp.presentation.viewmodel.PlayerViewModel
import dev.stranik.musicapp.presentation.viewmodel.SearchViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Library : Screen("library")
    object Player : Screen("player")

    object ArtistDetail {
        const val ROUTE = "artist/{artistId}"
        fun createRoute(artistId: String) = "artist/$artistId"
    }

    object PlaylistDetail {
        const val ROUTE = "playlist/{playlistId}"
        fun createRoute(playlistId: String) = "playlist/$playlistId"
    }
}

private data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Главная", Icons.Default.Home),
    BottomNavItem(Screen.Search, "Поиск", Icons.Default.Search),
    BottomNavItem(Screen.Library, "Медиатека", Icons.Default.LibraryMusic),
    BottomNavItem(Screen.Library, "Аккаунт", Icons.Default.AccountCircle),
)

@Composable
fun MusicNavGraph(
    context: Context, navController: NavHostController
) {
    val homeViewModel = viewModel<HomeViewModel>(factory = HomeViewModel.getViewModelFactory(context))
    val libraryViewModel = viewModel<LibraryViewModel>(factory = LibraryViewModel.getViewModelFactory(context))
    val playerViewModel = viewModel<PlayerViewModel>(factory = PlayerViewModel.getViewModelFactory(context))
    val searchViewModel = viewModel<SearchViewModel>(factory = SearchViewModel.getViewModelFactory(context))

    val playerState by playerViewModel.uiState.collectAsState()
    val currentRoute by navController.currentBackStackEntryAsState()

    Scaffold(
        bottomBar = {
            Column {
                // Мини-плеер над bottom bar
                if (playerState.currentTrack != null &&
                    currentRoute?.destination?.route != Screen.Player.route
                ) {
                    MiniPlayer(
                        state = playerState,
                        onPlayPause = playerViewModel::onPlayPause,
                        onClick = { navController.navigate(Screen.Player.route) }
                    )
                }
                BottomNavBar(navController = navController)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onTrackClick = playerViewModel::play,
                    onArtistClick = { id ->
                        navController.navigate(
                            Screen.ArtistDetail.createRoute(
                                id
                            )
                        )
                    },
                    onAlbumClick = { id ->
                        navController.navigate(
                            Screen.PlaylistDetail.createRoute(
                                id
                            )
                        )
                    }
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = searchViewModel,
                    onTrackClick = playerViewModel::play,
                    onArtistClick = { id ->
                        navController.navigate(
                            Screen.ArtistDetail.createRoute(
                                id
                            )
                        )
                    }
                )
            }
            composable(Screen.Library.route) {
                LibraryScreen(
                    viewModel = libraryViewModel,
                    onPlaylistClick = { id ->
                        navController.navigate(
                            Screen.PlaylistDetail.createRoute(
                                id
                            )
                        )
                    },
                    onTrackClick = playerViewModel::play
                )
            }
            composable(Screen.Player.route) {
                PlayerScreen(
                    viewModel = playerViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.ArtistDetail.ROUTE,
                arguments = listOf(navArgument("artistId") { type = NavType.StringType })
            ) { backStackEntry ->
                val artistId = backStackEntry.arguments?.getString("artistId") ?: return@composable
                // ArtistDetailScreen(artistId = artistId, viewModel = hiltViewModel())
            }
            composable(
                route = Screen.PlaylistDetail.ROUTE,
                arguments = listOf(navArgument("playlistId") { type = NavType.StringType })
            ) { backStackEntry ->
                val playlistId =
                    backStackEntry.arguments?.getString("playlistId") ?: return@composable
                // PlaylistDetailScreen(playlistId = playlistId, viewModel = hiltViewModel())
            }
        }
    }
}

@Composable
private fun BottomNavBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.screen.route,
                onClick = {
                    if (currentRoute != item.screen.route) {
                        navController.navigate(item.screen.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}