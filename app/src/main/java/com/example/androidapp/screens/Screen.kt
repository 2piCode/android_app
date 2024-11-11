package com.example.androidapp.screens

import com.example.androidapp.R

sealed class Screen(
    val title: String,
    val icon: Int = -1
) {
    data object Home : Screen("Home", icon = R.drawable.home_page_icon)
    data object List : Screen("List", icon = R.drawable.list_page_icon)
    data object Detail : Screen("Anime", icon = -1)
}


