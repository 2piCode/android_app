package com.example.androidapp.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.androidapp.R
import com.example.androidapp.model.Anime
import com.example.androidapp.model.AnimeViewModel

@Composable
fun FavoriteScreen(viewModel: AnimeViewModel, onClick: (Int) -> Unit) {
    val viewModelUiState by viewModel.uiState.collectAsState()
    val savedAnime: List<Anime> = viewModelUiState.savedAnime

    if (savedAnime.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(R.string.not_found_favorite_anime))
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(savedAnime) { anime ->
                AnimeListItem(
                    anime = anime,
                    isSaved = true,
                    onClick = { onClick(anime.id) },
                    onSaveClick = { viewModel.toggleSaveAnime(anime) }
                )
            }
        }
    }
}