package com.example.autotrolejapp.line_variant

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.autotrolejapp.database.LineDatabaseDao
import com.example.autotrolejapp.database.StationDatabaseDao


class LineVariantViewModelFactory(
    private val stationDatabaseDao: StationDatabaseDao,
    private val lineDatabaseDao: LineDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(LineVariantViewModel::class.java)) {
            return LineVariantViewModel(stationDatabaseDao, lineDatabaseDao, application) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
