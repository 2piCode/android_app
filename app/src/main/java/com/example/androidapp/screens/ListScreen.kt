package com.example.androidapp.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.androidapp.R
import com.example.androidapp.data.SearchNotification
import com.example.androidapp.model.Anime
import com.example.androidapp.model.AnimeViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ListScreen(viewModel: AnimeViewModel, searchNotification: SearchNotification, onClick: (Int) -> Unit) {
    val viewModelUiState by viewModel.uiState.collectAsState()
    val animeList: List<Anime> = viewModelUiState.animeList
    val searchQuery: String = viewModelUiState.searchQuery
    val searchResults: List<Anime> = viewModelUiState.searchResults
    val savedAnime: List<Anime> = viewModelUiState.savedAnime

    val context = LocalContext.current

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    var selectedStartDate by remember { mutableStateOf(viewModelUiState.startDate) }
    var selectedEndDate by remember { mutableStateOf(viewModelUiState.endDate) }

    fun showDatePickerDialog(
        initialDate: String,
        onDateSelected: (String) -> Unit,
        minDate: Long = 0L,
        maxDate: Long? = null
    ) {
        val calendar = Calendar.getInstance()
        if (initialDate.isNotEmpty()) {
            val date = dateFormat.parse(initialDate)
            date?.let { calendar.time = it }
        }

        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val newDate = Calendar.getInstance()
                newDate.set(year, month, dayOfMonth)
                val formattedDate = dateFormat.format(newDate.time)
                onDateSelected(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = minDate
            maxDate?.let { datePicker.maxDate = it }
            show()
        }
    }

    if (showStartDatePicker) {
        showDatePickerDialog(
            initialDate = selectedStartDate,
            onDateSelected = { newStartDate ->
                selectedStartDate = newStartDate
                viewModel.onSearchCriteriaChange(startDate = newStartDate)
                showStartDatePicker = false

                if (selectedEndDate.isNotEmpty()) {
                    val start = dateFormat.parse(newStartDate)
                    val end = dateFormat.parse(selectedEndDate)
                    if (start != null && end != null && start.after(end)) {
                        selectedEndDate = ""
                        viewModel.onSearchCriteriaChange(endDate = "")
                    }
                }
            },
            minDate = 0L,
            maxDate = selectedEndDate.takeIf { it.isNotEmpty() }?.let { dateFormat.parse(it)?.time }
        )
    }

    if (showEndDatePicker) {
        showDatePickerDialog(
            initialDate = selectedEndDate,
            onDateSelected = { newEndDate ->
                selectedEndDate = newEndDate
                viewModel.onSearchCriteriaChange(endDate = newEndDate)
                showEndDatePicker = false
            },
            minDate = selectedStartDate.takeIf { it.isNotEmpty() }
                ?.let { dateFormat.parse(it)?.time } ?: 0L
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange = { query ->
                viewModel.onSearchCriteriaChange(query = query)
            },
            onClearFilters = {
                viewModel.onSearchCriteriaChange(
                    query = "",
                    startDate = "",
                    endDate = ""
                )
            },
            onStartDateClick = { showStartDatePicker = true },
            onEndDateClick = { showEndDatePicker = true },
            startDate = selectedStartDate,
            endDate = selectedEndDate,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!searchNotification.isChangedSearch()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(animeList.take(AnimeViewModel.COUNT_ANIME_ON_PAGE)) { anime ->
                    AnimeListItem(anime = anime, isSaved = savedAnime.any { it.id == anime.id },
                        onClick = { onClick(anime.id) },
                        onSaveClick = { viewModel.toggleSaveAnime(anime) })
                }
            }
        } else {
            if (searchResults.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(searchResults) { anime ->
                        AnimeListItem(anime = anime, isSaved = savedAnime.any { it.id == anime.id },
                            onClick = { onClick(anime.id) },
                            onSaveClick = { viewModel.toggleSaveAnime(anime) })
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
fun AnimeListItem(anime: Anime, isSaved: Boolean, onClick: () -> Unit, onSaveClick: () -> Unit) {
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
                val imagePainter = rememberAsyncImagePainter(
                    model = if (!anime.localImagePath.isNullOrEmpty()) {
                        File(anime.localImagePath)
                    } else {
                        anime.fullImageUrl
                    }
                )

                Image(
                    painter = imagePainter,
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

                IconButton(onClick = { onSaveClick() }) {
                    if (isSaved) {
                        Icon(
                            painter = painterResource(id = R.drawable.heart),
                            contentDescription = stringResource(R.string.remove_from_favorites),
                            tint = Color.Red
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.heart_empty),
                            contentDescription = stringResource(R.string.add_to_favorites)
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearFilters: () -> Unit,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit,
    startDate: String,
    endDate: String,
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

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextButton(onClick = onStartDateClick) {
                Text(text = stringResource(R.string.start_data_text))
            }
            if (startDate.isNotEmpty()) {
                Text(
                    text = startDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextButton(onClick = onEndDateClick) {
                Text(text = stringResource(R.string.end_data_text))
            }
            if (endDate.isNotEmpty()) {
                Text(
                    text = endDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextButton(onClick = onClearFilters) {
            Text(stringResource(R.string.clear_filters))
        }
    }
}