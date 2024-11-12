package com.example.androidapp.model

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.androidapp.network.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "anime_preferences")

data class SearchDto(
    val query: String,
    val startDate: String,
    val endDate: String
)

class AnimeRepository(private val context: Context) {
    val searchFlow: Flow<SearchDto> = context.dataStore.data
        .map { preferences ->
            return@map SearchDto(
                query = preferences[QUERY_KEY] ?: "",
                startDate = preferences[START_DATE_KEY] ?: "",
                endDate = preferences[END_DATE_KEY] ?: ""
            )
        }

    suspend fun saveQuery(query: String) {
        context.dataStore.edit { preferences ->
            preferences[QUERY_KEY] = query
        }
    }

    suspend fun saveStartDate(date: String) {
        context.dataStore.edit { preferences ->
            preferences[START_DATE_KEY] = date
        }
    }

    suspend fun saveEndDate(date: String) {
        context.dataStore.edit { preferences ->
            preferences[END_DATE_KEY] = date
        }
    }

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

    companion object {
        val QUERY_KEY = stringPreferencesKey("query")
        val START_DATE_KEY = stringPreferencesKey("start_date")
        val END_DATE_KEY = stringPreferencesKey("end_date")
    }
}
