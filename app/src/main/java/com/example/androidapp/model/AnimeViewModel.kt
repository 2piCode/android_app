package com.example.androidapp.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.network.RetrofitInstance
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class AnimeViewModel : ViewModel() {
    private val _anime = MutableStateFlow<List<Anime>>(emptyList())
    val anime: StateFlow<List<Anime>> = _anime

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<Anime>>(emptyList())
    val searchResults: StateFlow<List<Anime>> = _searchResults

    private var searchJob: Job? = null

    init {
        loadInitialAnime()
    }

    private fun loadInitialAnime() {
        viewModelScope.launch {
            _isLoading.value = true

            val animeList: MutableList<Anime> = mutableListOf()
            var id = 0
            while (animeList.size < COUNT_ANIME_ON_PAGE) {
                try {
                    val response = RetrofitInstance.api.getAnimeDetails(id++)
                    val fetchedAnime = response.data.toAnime()
                    animeList.add(fetchedAnime)
                } catch (_: Exception) {
                    // Некоторые аниме могут быть недоступны, пропускаем их
                }
            }

            _isLoading.value = false
            _anime.value = animeList
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            searchAnime(query)
        }
    }

    private fun searchAnime(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _searchQuery.value = ""
            return
        }

        _searchQuery.value = query

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = RetrofitInstance.api.searchAnime(query)
                val results = response.data.map { it.toAnime() }
                _searchResults.value = results
                _anime.value = (_anime.value + results).distinctBy { it.id }
            } catch (e: HttpException) {
                when (e.code()) {
                    404 -> _errorMessage.value = "Аниме не найдено."
                    else -> _errorMessage.value = "Ошибка сети: ${e.localizedMessage}"
                }
                _searchResults.value = emptyList()
            } catch (e: IOException) {
                _errorMessage.value = "Проблемы с подключением к интернету."
                _searchResults.value = emptyList()
            } catch (e: Exception) {
                _errorMessage.value = "Неизвестная ошибка: ${e.localizedMessage}"
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    companion object {
        const val COUNT_ANIME_ON_PAGE = 5
    }
}