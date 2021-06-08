package com.example.autotrolejapp.map

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.autotrolejapp.database.StationDatabaseDao

class MapViewModelFactory(
    private val stationDatabaseDao: StationDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(stationDatabaseDao, application) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}