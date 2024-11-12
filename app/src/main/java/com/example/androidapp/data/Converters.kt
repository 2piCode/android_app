package com.example.androidapp.data

import androidx.room.TypeConverter
import com.example.androidapp.model.Genre
import com.example.androidapp.model.StreamingUrl
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromGenreList(genres: List<Genre>): String {
        return gson.toJson(genres)
    }

    @TypeConverter
    fun toGenreList(genresString: String): List<Genre> {
        val listType = object : TypeToken<List<Genre>>() {}.type
        return gson.fromJson(genresString, listType)
    }

    @TypeConverter
    fun fromStreamingUrlList(streamingUrls: List<StreamingUrl>?): String? {
        return gson.toJson(streamingUrls)
    }

    @TypeConverter
    fun toStreamingUrlList(streamingUrlsString: String?): List<StreamingUrl>? {
        if (streamingUrlsString == null) return null
        val listType = object : TypeToken<List<StreamingUrl>>() {}.type
        return gson.fromJson(streamingUrlsString, listType)
    }

    @TypeConverter
    fun fromGenre(genre: Genre): String {
        return genre.name
    }

    @TypeConverter
    fun toGenre(genreString: String): Genre {
        return Genre.valueOf(genreString)
    }
}