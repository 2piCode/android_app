package com.example.androidapp.presentation.profile.state

interface EditProfileState {
    var name: String
    var url: String
    var photoUri: android.net.Uri
    var isNeedToShowPermission: Boolean
    var isNeedToShowSelect: Boolean
}