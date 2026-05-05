package com.menowell.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.menowell.ui.components.MenoBottomNav
import com.menowell.ui.components.MenoBottomNavItem
import com.menowell.ui.screens.AuthScreen
import com.menowell.ui.screens.ChatScreen
import com.menowell.ui.screens.CheckInScreen
import com.menowell.ui.screens.HomeScreen
import com.menowell.ui.screens.InsightsScreen
import com.menowell.ui.screens.PatternsScreen
import com.menowell.ui.screens.ProfileScreen
import com.menowell.ui.screens.WeeklyLetterScreen
import com.menowell.viewmodel.AuthViewModel
import com.menowell.viewmodel.ChatViewModel
import com.menowell.viewmodel.CheckInViewModel
import com.menowell.viewmodel.LetterViewModel
import com.menowell.viewmodel.ProfileViewModel
import com.menowell.viewmodel.SessionUiState
import com.menowell.viewmodel.SessionViewModel

private sealed class Route(val route: String, val label: String) {
    data object Auth : Route("auth", "Auth")
    data object Home : Route("home", "Home")
    data object Chat : Route("chat", "Mia")
    data object CheckIn : Route("checkin", "Check-in")
    data object Letter : Route("letter", "Letter")
    data object Insights : Route("insights", "Insights")
    data object Patterns : Route("patterns", "Patterns")
    data object Profile : Route("profile", "Profile")
}

@Composable
fun MenoWellNavGraph() {
    val navController = rememberNavController()
    val items = listOf(
        MenoBottomNavItem(Route.Home.route, Route.Home.label, Icons.Default.Home),
        MenoBottomNavItem(Route.Chat.route, Route.Chat.label, Icons.Default.ChatBubbleOutline),
        MenoBottomNavItem(Route.CheckIn.route, Route.CheckIn.label, Icons.Default.Today),
        MenoBottomNavItem(Route.Letter.route, Route.Letter.label, Icons.AutoMirrored.Filled.MenuBook),
        MenoBottomNavItem(Route.Insights.route, Route.Insights.label, Icons.Outlined.AutoAwesome),
        MenoBottomNavItem(Route.Patterns.route, Route.Patterns.label, Icons.Outlined.BarChart),
        MenoBottomNavItem(Route.Profile.route, Route.Profile.label, Icons.Default.PersonOutline),
    )
    val sessionViewModel = hiltViewModel<SessionViewModel>()
    val sessionState by sessionViewModel.state.collectAsStateWithLifecycle()
    val current = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = current != Route.Auth.route && sessionState is SessionUiState.Authenticated

    LaunchedEffect(sessionState, current) {
        if (current == null) return@LaunchedEffect
        when (sessionState) {
            SessionUiState.Authenticated -> {
                if (current == Route.Auth.route) {
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Auth.route) { inclusive = true }
                    }
                }
            }
            SessionUiState.Unauthenticated -> {
                if (current != Route.Auth.route) {
                    navController.navigate(Route.Auth.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                }
            }
            SessionUiState.Loading -> Unit
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                MenoBottomNav(
                    items = items,
                    currentRoute = current,
                    onItemClick = { item ->
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Route.Auth.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Route.Auth.route) {
                AuthScreen(viewModel = hiltViewModel<AuthViewModel>(), onSuccess = {
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Auth.route) { inclusive = true }
                    }
                })
            }
            composable(Route.Home.route) {
                HomeScreen(
                    onGoChat = { navController.navigate(Route.Chat.route) },
                    onGoCheckIn = { navController.navigate(Route.CheckIn.route) },
                    onGoLetter = { navController.navigate(Route.Letter.route) },
                    onGoProfile = { navController.navigate(Route.Profile.route) },
                )
            }
            composable(Route.Chat.route) { ChatScreen(viewModel = hiltViewModel<ChatViewModel>()) }
            composable(Route.CheckIn.route) { CheckInScreen(viewModel = hiltViewModel<CheckInViewModel>()) }
            composable(Route.Letter.route) { WeeklyLetterScreen(viewModel = hiltViewModel<LetterViewModel>()) }
            composable(Route.Insights.route) { InsightsScreen() }
            composable(Route.Patterns.route) { PatternsScreen() }
            composable(Route.Profile.route) {
                ProfileScreen(
                    viewModel = hiltViewModel<ProfileViewModel>(),
                    onLogout = {
                        sessionViewModel.logout()
                        navController.navigate(Route.Auth.route) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        }
                    },
                )
            }
        }
    }
}
