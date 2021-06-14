package com.example.autotrolejapp.station

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.autotrolejapp.database.ScheduleStationDatabaseDao
import com.example.autotrolejapp.database.StationDatabaseDao

class StationViewModelFactory(
    private val stationDatabaseDao: StationDatabaseDao,
    private val scheduleStationDatabaseDao: ScheduleStationDatabaseDao,
    private val application: Application
): ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(StationViewModel::class.java)) {
            return StationViewModel(stationDatabaseDao, scheduleStationDatabaseDao, application) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
