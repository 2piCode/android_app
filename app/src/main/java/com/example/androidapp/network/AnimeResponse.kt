package com.example.androidapp.network

import com.squareup.moshi.Json

data class AnimeListResponse(
    @Json(name = "data") val data: List<AnimeData>
)

data class AnimeResponse(
    @Json(name = "data") val data: AnimeData
)

data class AnimeData(
    @Json(name = "mal_id") val malId: Int,
    @Json(name = "title") val title: String,
    @Json(name = "synopsis") val synopsis: String,
    @Json(name = "year") val year: Int?,
    @Json(name = "images") val images: Images,
    @Json(name = "genres") val genres: List<GenreResponse>,
    @Json(name = "external") val external: List<ExternalLink>? = null
)

data class Images(
    @Json(name = "jpg") val jpg: ImageSize,
    @Json(name = "webp") val webp: ImageSize
)

data class ImageSize(
    @Json(name = "image_url") val imageUrl: String,
    @Json(name = "small_image_url") val smallImageUrl: String,
    @Json(name = "large_image_url") val largeImageUrl: String
)

data class GenreResponse(
    @Json(name = "mal_id") val malId: Int,
    @Json(name = "type") val type: String,
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String
)

data class ExternalLink(
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String
)