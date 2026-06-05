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
import androidx.compose.runtime.produceState
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
import dev.stranik.musicapp.domain.Creator
import dev.stranik.musicapp.presentation.ui.screen.AccountScreen
import dev.stranik.musicapp.presentation.ui.screen.ArtistDetailScreen
import dev.stranik.musicapp.presentation.ui.screen.LoginScreen
import dev.stranik.musicapp.presentation.ui.screen.HomeScreen
import dev.stranik.musicapp.presentation.ui.screen.LibraryScreen
import dev.stranik.musicapp.presentation.ui.screen.PlayerScreen
import dev.stranik.musicapp.presentation.ui.screen.PlaylistDetailScreen
import dev.stranik.musicapp.presentation.ui.screen.RegistrationScreen
import dev.stranik.musicapp.presentation.ui.screen.SearchScreen
import dev.stranik.musicapp.presentation.viewmodel.AccountViewModel
import dev.stranik.musicapp.presentation.viewmodel.ArtistDetailViewModel
import dev.stranik.musicapp.presentation.viewmodel.HomeViewModel
import dev.stranik.musicapp.presentation.viewmodel.LibraryViewModel
import dev.stranik.musicapp.presentation.viewmodel.LoginViewModel
import dev.stranik.musicapp.presentation.viewmodel.PlayerViewModel
import dev.stranik.musicapp.presentation.viewmodel.PlaylistDetailViewModel
import dev.stranik.musicapp.presentation.viewmodel.RegistrationViewModel
import dev.stranik.musicapp.presentation.viewmodel.SearchViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Registration : Screen("registration")
    object Account : Screen("account")
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
    BottomNavItem(Screen.Account, "Аккаунт", Icons.Default.AccountCircle),
)

private val authRoutes = setOf(Screen.Login.route, Screen.Registration.route)

@Composable
fun MusicNavGraph(
    context: Context, navController: NavHostController
) {
    val isLoggedIn by produceState<Boolean?>(initialValue = null) {
        val tokenManager = Creator.provideTokenManager(context)
        value = tokenManager.getAccessToken() != null
    }

    if (isLoggedIn == null) return

    val playerViewModel = viewModel<PlayerViewModel>(factory = PlayerViewModel.getViewModelFactory(context))
    val playerState by playerViewModel.uiState.collectAsState()

    val currentRoute by navController.currentBackStackEntryAsState()
    val route = currentRoute?.destination?.route
    val showBottomBar = route !in authRoutes

    val startDestination = if (isLoggedIn == true) Screen.Home.route else Screen.Login.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Column {
                    if (playerState.currentTrack != null && route != Screen.Player.route) {
                        MiniPlayer(
                            state = playerState,
                            onPlayPause = playerViewModel::onPlayPause,
                            onSkipNext = playerViewModel::onSkipNext,
                            onSkipPrevious = playerViewModel::onSkipPrevious,
                            onClick = { navController.navigate(Screen.Player.route) }
                        )
                    }
                    BottomNavBar(navController = navController)
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Login.route) {
                val loginViewModel = viewModel<LoginViewModel>(factory = LoginViewModel.getViewModelFactory(context))
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToRegistration = {
                        navController.navigate(Screen.Registration.route)
                    }
                )
            }
            composable(Screen.Registration.route) {
                val registrationViewModel = viewModel<RegistrationViewModel>(factory = RegistrationViewModel.getViewModelFactory(context))
                RegistrationScreen(
                    viewModel = registrationViewModel,
                    onRegistrationSuccess = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Registration.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Screen.Account.route) {
                val accountViewModel = viewModel<AccountViewModel>(factory = AccountViewModel.getViewModelFactory(context))
                
                AccountScreen(
                    viewModel = accountViewModel,
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onEdit = {
                        navController.navigate(Screen.Registration.route)
                    }
                )
            }
            composable(Screen.Home.route) {
                val homeViewModel = viewModel<HomeViewModel>(factory = HomeViewModel.getViewModelFactory(context))
                HomeScreen(
                    viewModel = homeViewModel,
                    onTrackClick = { track -> playerViewModel.play(track.id) },
                    onArtistClick = { id -> navController.navigate(Screen.ArtistDetail.createRoute(id)) },
                    onAlbumClick = { id -> navController.navigate(Screen.PlaylistDetail.createRoute(id)) }
                )
            }
            composable(Screen.Search.route) {
                val searchViewModel = viewModel<SearchViewModel>(factory = SearchViewModel.getViewModelFactory(context))
                SearchScreen(
                    viewModel = searchViewModel,
                    onTrackClick = { track -> playerViewModel.play(track.id) },
                    onArtistClick = { id -> navController.navigate(Screen.ArtistDetail.createRoute(id)) }
                )
            }
            composable(Screen.Library.route) {
                val libraryViewModel = viewModel<LibraryViewModel>(factory = LibraryViewModel.getViewModelFactory(context))
                LibraryScreen(
                    viewModel = libraryViewModel,
                    onPlaylistClick = { id -> navController.navigate(Screen.PlaylistDetail.createRoute(id)) },
                    onTrackClick = { track -> playerViewModel.play(track.id) }
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
                val artistDetailViewModel = viewModel<ArtistDetailViewModel>(
                    key = artistId,
                    factory = ArtistDetailViewModel.getViewModelFactory(artistId)
                )

                ArtistDetailScreen(
                    viewModel = artistDetailViewModel,
                    onBack = { navController.popBackStack() },
                    onTrackClick = { track -> playerViewModel.play(track.id) }
                )
            }
            composable(
                route = Screen.PlaylistDetail.ROUTE,
                arguments = listOf(navArgument("playlistId") { type = NavType.StringType })
            ) { backStackEntry ->
                val playlistId = backStackEntry.arguments?.getString("playlistId") ?: return@composable

                val libraryRepository = Creator.provideLibraryRepository()
                val trackRepository = Creator.provideTrackRepository()
                val playerRepository = Creator.providePlayerRepository(context)
                val playPlaylistUseCase = Creator.providePlayPlaylistUseCase(playerRepository, trackRepository)

                val playlistDetailViewModel = viewModel<PlaylistDetailViewModel>(
                    key = playlistId,
                    factory = PlaylistDetailViewModel.getViewModelFactory(
                        playlistId = playlistId,
                        libraryRepository = libraryRepository,
                        trackRepository = trackRepository,
                        playPlaylistUseCase = playPlaylistUseCase
                    )
                )

                PlaylistDetailScreen(
                    viewModel = playlistDetailViewModel,
                    onBack = { navController.popBackStack() }
                )
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
