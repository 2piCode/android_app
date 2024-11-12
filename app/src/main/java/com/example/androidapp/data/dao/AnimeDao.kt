package com.example.androidapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.androidapp.model.Anime
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnime(anime: Anime)

    @Delete
    suspend fun deleteAnime(anime: Anime)

    @Query("SELECT * FROM anime")
    fun getAllSavedAnime(): Flow<List<Anime>>

    @Query("SELECT EXISTS(SELECT 1 FROM anime WHERE id = :animeId)")
    suspend fun isAnimeSaved(animeId: Int): Boolean

    @Update
    suspend fun updateAnime(anime: Anime)
}