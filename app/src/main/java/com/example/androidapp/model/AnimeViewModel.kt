package com.example.androidapp.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class AnimeViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository: AnimeRepository = AnimeRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(AnimeUiState())
    val uiState: StateFlow<AnimeUiState> = _uiState

    private var searchJob: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        when (exception) {
            is HttpException -> {
                val message = when (exception.code()) {
                    404 -> "Аниме не найдено."
                    else -> "Ошибка сети: ${exception.localizedMessage}"
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = message
                    )
                }
            }

            is IOException -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Проблемы с подключением к интернету."
                    )
                }
            }

            is Exception -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Неизвестная ошибка: ${exception.localizedMessage}"
                    )
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            repository.searchFlow.collect { flow ->
                if (flow.query.isBlank() && flow.startDate.isBlank() && flow.endDate.isBlank()) {
                    loadInitialAnime()
                } else {
                    onSearchCriteriaChange(
                        flow.query,
                        flow.startDate,
                        flow.endDate
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.getAllSavedAnime().collect { savedList ->
                _uiState.value = _uiState.value.copy(savedAnime = savedList)
            }
        }
    }

    private fun loadInitialAnime() {
        viewModelScope.launch(exceptionHandler) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val animeList = mutableListOf<Anime>()
            var id = 0

            while (animeList.size < COUNT_ANIME_ON_PAGE) {
                try {
                    val fetchedAnime = repository.getAnimeDetails(id++)
                    animeList.add(fetchedAnime)
                } catch (_: JsonDataException) {
                } catch (_: HttpException) {
                    // Некоторые аниме могут быть недоступны, пропускаем их
                }
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    animeList = animeList
                )
            }
        }
    }

    fun onSearchCriteriaChange(
        query: String = _uiState.value.searchQuery,
        startDate: String = _uiState.value.startDate,
        endDate: String = _uiState.value.endDate
    ) {
        viewModelScope.launch {
            repository.saveQuery(query);
            repository.saveStartDate(startDate)
            repository.saveEndDate(endDate)
            _uiState.update {
                it.copy(
                    searchQuery = query,
                    startDate = startDate,
                    endDate = endDate
                )
            }
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            searchAnime(query, startDate, endDate)
        }
    }

    private fun searchAnime(query: String, startDate: String, endDate: String) {
        if (query.isBlank() && startDate.isBlank() && endDate.isBlank()) {
            _uiState.update {
                it.copy(
                    searchResults = emptyList(),
                    searchQuery = ""
                )
            }
            return
        }

        viewModelScope.launch(exceptionHandler) {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    searchResults = emptyList()
                )
            }

            val results = repository.searchAnime(query, startDate, endDate)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    searchResults = results,
                    animeList = (it.animeList + results).distinctBy { sIt -> sIt.id }
                )
            }
        }
    }

    fun toggleSaveAnime(anime: Anime) {
        viewModelScope.launch {
            repository.isAnimeSaved(anime.id).let { isSaved ->
                if (isSaved) {
                    repository.removeAnime(anime)
                } else {
                    repository.saveAnime(anime)
                }
            }
        }
    }

    companion object {
        const val COUNT_ANIME_ON_PAGE = 5
    }
}