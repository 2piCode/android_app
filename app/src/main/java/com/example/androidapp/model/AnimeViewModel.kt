package com.example.androidapp.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class AnimeViewModel(private val repository: AnimeRepository = AnimeRepository()) : ViewModel() {
    private val _uiState = MutableStateFlow(AnimeUiState())
    val uiState: StateFlow<AnimeUiState> = _uiState

    private var searchJob: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = false,
                errorMessage = "Неизвестная ошибка: ${throwable.localizedMessage}"
            )
        }
    }

    init {
        loadInitialAnime()
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
                } catch (_: Exception) {
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

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            searchAnime(query)
        }
    }

    private fun searchAnime(query: String) {
        if (query.isBlank()) {
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

            try {
                val results = repository.searchAnime(query)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        searchResults = results,
                        animeList = (it.animeList + results).distinctBy { sIt -> sIt.id }
                    )
                }
            } catch (e: HttpException) {
                val message = when (e.code()) {
                    404 -> "Аниме не найдено."
                    else -> "Ошибка сети: ${e.localizedMessage}"
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = message
                    )
                }
            } catch (e: IOException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Проблемы с подключением к интернету."
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Неизвестная ошибка: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    companion object {
        const val COUNT_ANIME_ON_PAGE = 5
    }
}