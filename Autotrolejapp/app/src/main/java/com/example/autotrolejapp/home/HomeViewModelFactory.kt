package com.example.autotrolejapp.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.autotrolejapp.database.LineDatabaseDao
import com.example.autotrolejapp.database.LineStationDatabaseDao
import com.example.autotrolejapp.database.StationDatabaseDao

class HomeViewModelFactory(
    private val lineDatabaseDao: LineDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(lineDatabaseDao, application) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}