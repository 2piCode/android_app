package com.example.androidapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.androidapp.R
import com.example.androidapp.model.Anime
import com.example.androidapp.model.AnimeViewModel

@Composable
fun ListScreen(viewModel: AnimeViewModel, onClick: (Int) -> Unit) {
    val viewModelUiState by viewModel.uiState.collectAsState()
    val animeList: List<Anime> = viewModelUiState.animeList
    val searchQuery: String = viewModelUiState.searchQuery
    val searchResults: List<Anime> = viewModelUiState.searchResults

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.onSearchQueryChange(it) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (searchQuery.isBlank()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(animeList.take(AnimeViewModel.COUNT_ANIME_ON_PAGE)) { anime ->
                    AnimeListItem(anime = anime, onClick = { onClick(anime.id) })
                }
            }
        } else {
            if (searchResults.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(searchResults) { anime ->
                        AnimeListItem(anime = anime, onClick = { onClick(anime.id) })
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "${stringResource(R.string.search_not_found_result)} \"$searchQuery\"")
                }
            }
        }
    }
}

@Composable
fun AnimeListItem(anime: Anime, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp),
        headlineContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Black, MaterialTheme.shapes.medium)
                    .wrapContentHeight(),
                verticalAlignment = Alignment.Top,
            ) {
                Image(
                    painter = rememberAsyncImagePainter(anime.fullImageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(150.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Text(anime.title, style = MaterialTheme.typography.titleLarge)
                    Text(
                        anime.synopsis,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 4,
                        color = Color.Gray,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    )
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = { onQueryChange(it) },
        label = { Text(text = stringResource(R.string.search_text_without_query)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search_icon_description)
            )
        },
        singleLine = true,
        modifier = modifier
    )
}