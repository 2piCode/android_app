package com.example.androidapp.model

import com.example.androidapp.network.RetrofitInstance

class AnimeRepository {
    suspend fun getAnimeDetails(id: Int): Anime {
        return RetrofitInstance.api.getAnimeDetails(id).data.toAnime()
    }

    suspend fun searchAnime(query: String): List<Anime> {
        return RetrofitInstance.api.searchAnime(query).data.map { it.toAnime() }
    }
}
