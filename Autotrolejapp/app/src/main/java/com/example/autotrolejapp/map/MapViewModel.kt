package com.example.autotrolejapp.map

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.autotrolejapp.database.StationDatabaseDao
import com.example.autotrolejapp.entities.BusLocation
import com.example.autotrolejapp.network.AutotrolejApi
import com.example.autotrolejapp.network.formatBusLocationResponse
import kotlinx.coroutines.launch


class MapViewModel(
    val stationDatabaseDao: StationDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    val stations = stationDatabaseDao.getAll()

    private val _busLocation = MutableLiveData<List<BusLocation>>()
    val busLocation: LiveData<List<BusLocation>>
        get() = _busLocation

    init {
        _busLocation.value = ArrayList()
        //TODO: use BusLocation later whene we will have a line with stations
        //getAutotrolejBusLocations()
    }

    private fun getAutotrolejBusLocations() {
        viewModelScope.launch {
            Log.d(className, "Request for autotrolej bus location")
            try {
                val data = AutotrolejApi.retrofitService.getCurrentBusLocations()
                _busLocation.value = formatBusLocationResponse(data)

                // val busLocationItem = _busLocation.value!!.first()
                // val lines = scheduleLineDatabaseDao.getLineByStart(busLocationItem.startId)

                Log.d(className, "DONE")
            } catch (e: Exception) {
                Log.e(className, "FAIL")
            }
        }
    }

    companion object {
        const val className = "MapViewModel"
    }


}