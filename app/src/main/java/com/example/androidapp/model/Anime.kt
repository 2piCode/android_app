package com.example.androidapp.model

enum class Genre(val s: String) {
    ACTION("Action"),
    ADVENTURE("Adventure"),
    COMEDY("Comedy"),
    DRAMA("Drama"),
    FANTASY("Fantasy"),
    HORROR("Horror"),
    MYSTERY("Mystery"),
    PSYCHOLOGICAL("Psychological"),
    ROMANCE("Romance"),
    SCIENCE_FICTION("Science Fiction"),
    THRILLER("Thriller"),
    WESTERN("Western"),
    SCI_FI("Sci-Fi"),
    AWARD_WINNING("Award Winning"),
    UNKNOWN("Unknown");

    companion object {
        fun fromString(name: String): Genre {
            return entries.find { it.s.equals(name, ignoreCase = true) } ?: UNKNOWN
        }
    }
}

data class StreamingUrl(
    val name: String = "",
    val url: String = ""
)

data class Anime(
    val id: Int,
    val title: String,
    val synopsis: String,
    val fullImageUrl: String,
    val smallImageUrl: String,
    val year: Int,
    val genres: List<Genre>,
    val streamingUrls: List<StreamingUrl>?
)