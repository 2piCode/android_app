package com.example.androidapp.presentation.profile.state

import java.time.LocalTime

interface EditProfileState {
    val name: String
    val url: String
    val photoUri: android.net.Uri
    val isNeedToShowPermission: Boolean
    val isNeedToShowSelect: Boolean
    val time: LocalTime
    val timeString: String
    val timeError: String?
    val isNeedToShowTimePicker: Boolean
}