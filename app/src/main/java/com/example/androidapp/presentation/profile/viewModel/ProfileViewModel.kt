package com.example.androidapp.presentation.profile.viewModel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.androidapp.data.repository.ProfileRepository
import com.example.androidapp.presentation.profile.state.ProfileState
import kotlinx.coroutines.launch

class ProfileViewModel(
    application: Application,
    private val repository: ProfileRepository
): AndroidViewModel(application) {
    private val mutableState = MutableProfileState()
    val viewState = mutableState as ProfileState

    init {
        viewModelScope.launch {
            repository.observeProfile().collect {
                mutableState.name = it.name
                mutableState.photoUri = Uri.parse(it.photoUri)
                mutableState.resumeUrl = it.url
            }
        }
    }

    class Factory(private val repository: ProfileRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(Application(), repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private class MutableProfileState: ProfileState {
        override var name by mutableStateOf("")
        override var photoUri by mutableStateOf(Uri.EMPTY)
        override var resumeUrl by mutableStateOf("")
    }
}