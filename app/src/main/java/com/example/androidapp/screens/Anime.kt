package com.example.androidapp.screens

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
    AWARD_WINNING("Award Winning")
}

data class Anime(
    val id: Int,
    val title: String,
    val synopsis: String,
    val fullImageUrl: String,
    val smallImageUrl: String,
    val year: Int,
    val genres: List<Genre>,
    val streamingUrls: List<String>
)