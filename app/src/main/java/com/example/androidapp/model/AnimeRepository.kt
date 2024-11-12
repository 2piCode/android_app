package com.example.androidapp.model

import com.example.androidapp.network.RetrofitInstance

class AnimeRepository {
    suspend fun getAnimeDetails(id: Int): Anime {
        return RetrofitInstance.api.getAnimeDetails(id).data.toAnime()
    }

    suspend fun searchAnime(
        query: String,
        startDate: String,
        endDate: String
    ): List<Anime> {
        val response = RetrofitInstance.api.searchAnime(
            query = query,
            startDate = startDate,
            endDate = endDate
        )
        return response.data.map { it.toAnime() }
    }
}
