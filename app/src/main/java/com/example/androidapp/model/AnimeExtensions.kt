package com.example.androidapp.model

import com.example.androidapp.network.AnimeData

fun AnimeData.toAnime(): Anime {
    return Anime(
        id = this.malId,
        title = this.title,
        synopsis = this.synopsis,
        smallImageUrl = this.images.jpg.smallImageUrl,
        fullImageUrl = this.images.jpg.largeImageUrl,
        year = this.year ?: 0,
        genres = this.genres.map { Genre.fromString(it.name) },
        streamingUrls = this.external?.map { StreamingUrl(it.name, it.url) } ?: emptyList()
    )
}