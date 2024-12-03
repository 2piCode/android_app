package com.example.androidapp.presentation.profile.state

import android.net.Uri

interface ProfileState {
    var name: String
    var photoUri: Uri
    var resumeUrl: String
}