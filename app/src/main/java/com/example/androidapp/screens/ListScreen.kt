package com.example.androidapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun ListScreen(viewModel: AnimeViewModel, onClick: (Int) -> Unit) {
                AnimeListItem(anime = anime, onClick = { onClick(anime.id) })
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