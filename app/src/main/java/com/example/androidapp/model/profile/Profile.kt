package com.example.androidapp.model.profile

import android.net.Uri

data class Profile(
    val fullName: String = "",
    val avatarUri: Uri? = null,
    val resumeUrl: String = "",
    val position: String = ""
)