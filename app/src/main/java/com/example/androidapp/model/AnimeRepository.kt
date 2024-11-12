package com.example.androidapp.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.androidapp.data.database.AnimeDatabase
import com.example.androidapp.network.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream

private val Context.dataStore by preferencesDataStore(name = "anime_preferences")

data class SearchDto(
    val query: String,
    val startDate: String,
    val endDate: String
)

class AnimeRepository(private val context: Context) {
    private val animeDao = AnimeDatabase.getDatabase(context).animeDao()
    private val imageLoader: ImageLoader = ImageLoader.Builder(context)
        .crossfade(true)
        .build()

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
        val localPath = downloadAndSaveImageWithCoil(anime.fullImageUrl, anime.id)
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

    private suspend fun downloadAndSaveImageWithCoil(imageUrl: String, animeId: Int): String? {
        return try {
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false)
                .build()

            val result = imageLoader.execute(request)

            val bitmap = if (result is SuccessResult) {
                (result.drawable as? BitmapDrawable)?.bitmap
            } else {
                null
            }

            bitmap?.let {
                saveBitmapLocally(it, animeId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun saveBitmapLocally(bitmap: Bitmap, animeId: Int): String {
        val filename = "anime_image_$animeId.jpg"
        val file = File(context.filesDir, filename)
        FileOutputStream(file).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        }
        return file.absolutePath
    }


    fun getAllSavedAnime(): Flow<List<Anime>> = animeDao.getAllSavedAnime()

    suspend fun isAnimeSaved(animeId: Int): Boolean = animeDao.isAnimeSaved(animeId)

    companion object {
        val QUERY_KEY = stringPreferencesKey("query")
        val START_DATE_KEY = stringPreferencesKey("start_date")
        val END_DATE_KEY = stringPreferencesKey("end_date")
    }
}
