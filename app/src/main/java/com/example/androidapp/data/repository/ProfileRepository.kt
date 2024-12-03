package com.example.androidapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import com.example.android.domain.model.ProfileEntity
import com.example.androidapp.data.dataSource.DataSourceProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalTime

class ProfileRepository(context: Context) {
    private val dataStore : DataStore<ProfileEntity> = DataSourceProvider(context).provide()

    suspend fun observeProfile(): Flow<ProfileEntity> = dataStore.data

    suspend fun getProfile(): ProfileEntity? = dataStore.data.firstOrNull()

    suspend fun setProfile(photoUri: String, name: String, url: String, time: LocalTime) =
        dataStore.updateData {
            it.toBuilder().apply {
                this.photoUri = photoUri
                this.name = name
                this.url = url
                this.time = time.toString()
            }.build()
        }
}