package com.example.androidapp.screens

import com.example.androidapp.R
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter

@Composable
fun DetailScreen(anime: Anime) {
    val context: Context = LocalContext.current
    val scrollState: ScrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val (imageRef, titleRef, synopsisRef, yearRef, genresRef, streamingRef) = createRefs()

            Image(
                painter = rememberAsyncImagePainter(anime.fullImageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(300.dp)
                    .constrainAs(imageRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            Text(
                text = anime.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(titleRef) {
                        top.linkTo(imageRef.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                textAlign = TextAlign.Center
            )

            Text(
                text = processSynopsis(anime.synopsis),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(synopsisRef) {
                        top.linkTo(titleRef.bottom, margin = 8.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            Text(
                text = "${stringResource(R.string.release_year)}: ${anime.year}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(yearRef) {
                        top.linkTo(synopsisRef.bottom, margin = 8.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                textAlign = TextAlign.Start
            )

            Text(
                text = "${stringResource(R.string.genres_string)}: ${anime.genres.joinToString(", ") { it.s }}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(genresRef) {
                        top.linkTo(yearRef.bottom, margin = 8.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                textAlign = TextAlign.Start
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(streamingRef) {
                        top.linkTo(genresRef.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Text(
                    text = "${stringResource(R.string.hosting_urls)}:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                anime.streamingUrls.forEach { url ->
                    SourceLink(url, context)
                }
            }
        }
    }
}

@Composable
fun SourceLink(url: String, context: Context) {
    Text(
        text = url,
        color = Color.Blue,
        textDecoration = TextDecoration.Underline,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
            .padding(vertical = 4.dp),
        textAlign = TextAlign.Start
    )
}

fun processSynopsis(synopsis: String): String {
    val intermediate = synopsis.replace("\\n\\n", "\n\n")
    return replaceLastOccurrence(intermediate, "\n\n", "")
}

fun replaceLastOccurrence(source: String, target: String, replacement: String): String {
    val lastIndex = source.lastIndexOf(target)
    return if (lastIndex == -1) {
        source
    } else {
        source.substring(0, lastIndex) + replacement + source.substring(lastIndex + target.length)
    }
}