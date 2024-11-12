package com.example.androidapp.data

import com.example.androidapp.model.AnimeViewModel

class SearchNotification(
    private val viewModel: AnimeViewModel
) {

    fun isChangedSearch(): Boolean {
        return viewModel.uiState.value.searchQuery.isNotBlank() ||
                viewModel.uiState.value.startDate.isNotBlank() ||
                viewModel.uiState.value.endDate.isNotBlank()
    }
}