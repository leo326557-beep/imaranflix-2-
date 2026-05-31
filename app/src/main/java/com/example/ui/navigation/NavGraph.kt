package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.di.AppContainer
import com.example.di.ViewModelFactoryHelper
import com.example.ui.auth.AuthViewModel
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.RegisterScreen
import com.example.ui.details.MovieDetailsScreen
import com.example.ui.details.MovieDetailsViewModel
import com.example.ui.home.HomeScreen
import com.example.ui.home.HomeViewModel
import com.example.ui.player.VideoPlayerScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.splash.SplashScreen
import com.example.ui.search.SearchScreen
import com.example.ui.search.SearchViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Search : Screen("search")
    object MovieDetails : Screen("movie_details/{movieId}") {
        fun createRoute(movieId: Int) = "movie_details/$movieId"
    }
    object VideoPlayer : Screen("video_player/{movieId}") {
        fun createRoute(movieId: Int) = "video_player/$movieId"
    }
    object Profile : Screen("profile")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    container: AppContainer,
    modifier: Modifier = Modifier
) {
    val factory = ViewModelFactoryHelper.provideFactory(container)
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    // We can scope HomeViewModel to NavGraph or just grab it
    val homeViewModel: HomeViewModel = viewModel(factory = factory)
    val searchViewModel: SearchViewModel = viewModel(factory = factory)
    val movieDetailsViewModel: MovieDetailsViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            val isLoggedIn by authViewModel.isUserLoggedInFlow.collectAsState(initial = authViewModel.isUserLoggedIn)
            // Wait for splash animation then use isLoggedIn
            SplashScreen(
                isUserLoggedIn = isLoggedIn,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToDetails = { movieId ->
                    navController.navigate(Screen.MovieDetails.createRoute(movieId))
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                viewModel = searchViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetails = { movieId ->
                    navController.navigate(Screen.MovieDetails.createRoute(movieId))
                }
            )
        }
        composable(Screen.MovieDetails.route) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull() ?: 0
            MovieDetailsScreen(
                movieId = movieId,
                viewModel = movieDetailsViewModel,
                onNavigateBack = { navController.popBackStack() },
                onPlayMovie = { id ->
                    navController.navigate(Screen.VideoPlayer.createRoute(id))
                }
            )
        }
        composable(Screen.VideoPlayer.route) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull() ?: 0
            VideoPlayerScreen(
                movieId = movieId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onLogoutSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) // pop everything
                    }
                }
            )
        }
    }
}
