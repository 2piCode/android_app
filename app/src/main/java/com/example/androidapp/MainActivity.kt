package com.example.androidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.androidapp.data.SearchNotification
import com.example.androidapp.model.AnimeViewModel
import com.example.androidapp.screens.DetailScreen
import com.example.androidapp.screens.FavoriteScreen
import com.example.androidapp.screens.HomeScreen
import com.example.androidapp.screens.ListScreen
import com.example.androidapp.screens.Screen
import com.example.androidapp.ui.theme.AndroidAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidAppTheme {
                MainScreen()
            }
        }
    }
}

@Composable
@Preview
fun MainScreen() {
    val navController = rememberNavController()
    val animeViewModel: AnimeViewModel = viewModel()
    val searchNotification = remember { SearchNotification(animeViewModel) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = when {
        currentRoute == null -> true
        currentRoute.startsWith(Screen.Detail.title) -> false
        else -> true
    }

    val viewModelUiState by animeViewModel.uiState.collectAsState()

    val isSearchChanged = searchNotification.isChangedSearch()

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomBar(
                    navController = navController,
                    currentRoute = currentRoute ?: "",
                    isSearchChanged = isSearchChanged
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.title,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.title) { HomeScreen() }
            composable(Screen.List.title) {
                if (viewModelUiState.errorMessage != null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Ошибка при подключении: ${viewModelUiState.errorMessage}")
                    }
                    return@composable
                }

                if (viewModelUiState.isLoading) {
                    FullScreenProgress()
                    return@composable
                }

                ListScreen(animeViewModel, searchNotification) { animeId ->
                    navController.navigate(Screen.Detail.title + "/$animeId") {
                    }
                }
            }
            composable(Screen.Favorite.title) {
                FavoriteScreen(animeViewModel) { animeId ->
                    navController.navigate(Screen.Detail.title + "/$animeId") {
                    }
                }
            }
            composable(Screen.Detail.title + "/{${R.string.detail_screen_id}}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("${R.string.detail_screen_id}")
                    ?.toIntOrNull()
                DetailScreen(id, animeViewModel, onClickPreviousButton = {
                    navController.popBackStack()
                })
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavController, currentRoute: String, isSearchChanged: Boolean) {
    val bottomItems = listOf(
        Screen.Home,
        Screen.List,
        Screen.Favorite
    )

    NavigationBar {
        bottomItems.forEach { screen ->
            NavigationBarItem(
                selected = screen.title == currentRoute,
                onClick = {
                    navController.navigate(screen.title)
                },
                label = { Text(screen.title) },
                icon = {
                    if (screen == Screen.List && isSearchChanged) {
                        BadgedBox(
                            badge = {
                                Badge()
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = screen.icon),
                                contentDescription = screen.title
                            )
                        }
                    } else {
                        Icon(
                            painter = painterResource(id = screen.icon),
                            contentDescription = screen.title
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun FullScreenProgress() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = { }
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(40.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}