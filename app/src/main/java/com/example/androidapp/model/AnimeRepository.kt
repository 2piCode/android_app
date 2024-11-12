package com.example.androidapp.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.androidapp.data.database.AnimeDatabase
import com.example.androidapp.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

private val Context.dataStore by preferencesDataStore(name = "anime_preferences")

data class SearchDto(
    val query: String,
    val startDate: String,
    val endDate: String
)

class AnimeRepository(private val context: Context) {
    private val animeDao = AnimeDatabase.getDatabase(context).animeDao()

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

    suspend fun saveAnime(anime: Anime) {
        val localPath = downloadAndSaveImage(anime.fullImageUrl, anime.id)
        val animeWithLocalPath = anime.copy(localImagePath = localPath)
        animeDao.insertAnime(animeWithLocalPath)
    }

    suspend fun removeAnime(anime: Anime) {
        try {
            animeDao.deleteAnime(anime)
            anime.localImagePath?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun downloadAndSaveImage(imageUrl: String, animeId: Int): String? {
        return try {
            val bitmap = BitmapFactory.decodeStream(withContext(Dispatchers.IO) {
                URL(imageUrl).openStream()
            })

            withContext(Dispatchers.IO) {
                val filename = "anime_image_$animeId.jpg"
                val file = File(context.filesDir, filename)
                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()
                file.absolutePath
            }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getAllSavedAnime(): Flow<List<Anime>> = animeDao.getAllSavedAnime()

    suspend fun isAnimeSaved(animeId: Int): Boolean = animeDao.isAnimeSaved(animeId)

    companion object {
        val QUERY_KEY = stringPreferencesKey("query")
        val START_DATE_KEY = stringPreferencesKey("start_date")
        val END_DATE_KEY = stringPreferencesKey("end_date")
    }
}
