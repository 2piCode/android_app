package com.example.androidapp.model

data class AnimeUiState(
    val animeList: List<Anime> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val searchResults: List<Anime> = emptyList(),
    val startDate: String = "",
    val endDate: String = "",
    val savedAnime: List<Anime> = emptyList()
)