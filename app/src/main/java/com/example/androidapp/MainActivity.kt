package com.example.androidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.androidapp.screens.Anime
import com.example.androidapp.screens.AnimeViewModel
import com.example.androidapp.screens.DetailScreen
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

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = when {
        currentRoute == null -> true
        currentRoute.startsWith(Screen.Detail.title) -> false
        else -> true
    }
    val showTopBar = currentRoute?.startsWith(Screen.Detail.title) ?: false

    Scaffold(
        topBar = @Composable {
            if (showTopBar) {
                val id = navBackStackEntry?.arguments?.getString("${R.string.detail_screen_id}")
                val anime = id?.let { animeViewModel.anime.findLast { it.id.toString() == id } }

                anime?.let {
                    TopBar(navController, it.title)
                }
            }
        },

        bottomBar = {
            if (showBottomBar) {
                BottomBar(navController = navController, currentRoute = currentRoute ?: "")
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
                ListScreen(animeViewModel) { animeId ->
                    navController.navigate(Screen.Detail.title + "/$animeId") {
                    }
                }
            }
            composable(Screen.Detail.title + "/{${R.string.detail_screen_id}}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("${R.string.detail_screen_id}")
                val anime: Anime =
                    animeViewModel.anime.findLast { it.id.toString() == id } ?: return@composable
                DetailScreen(anime)
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavController, currentRoute: String) {
    val bottomItems = listOf(
        Screen.Home,
        Screen.List
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
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = screen.title
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController, title: String) {
    TopAppBar(
        title = {
            Text(
                text = title,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = stringResource(R.string.back_arrow_description),
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.height(70.dp)
    )
}