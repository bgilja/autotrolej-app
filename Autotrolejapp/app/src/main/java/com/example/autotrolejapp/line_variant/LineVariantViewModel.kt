package com.example.autotrolejapp.line_variant

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.autotrolejapp.database.LineDatabaseDao
import com.example.autotrolejapp.database.StationDatabaseDao
import com.example.autotrolejapp.entities.BusLocation
import com.example.autotrolejapp.entities.Station
import com.example.autotrolejapp.network.AutotrolejApi
import com.example.autotrolejapp.network.formatBusLocationResponse
import kotlinx.coroutines.*

class LineVariantViewModel(
    val stationDatabaseDao: StationDatabaseDao,
    val lineDatabaseDao: LineDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    val stations = stationDatabaseDao.getAll()
    val liveStations: MutableLiveData<List<Station>> = MutableLiveData(listOf())

    private val _busLocation = MutableLiveData<List<BusLocation>>()
    val busLocation: LiveData<List<BusLocation>>
        get() = _busLocation

    init {
        _busLocation.value = ArrayList()
        //TODO: use BusLocation later whene we will have a line with stations
        getAutotrolejBusLocations()

    }

    //TODO: zavrsit, zasad bus se pinga svakih 3 sekunde, kad se izade iz viewScope -> ugasi se coroutina
    private fun getAutotrolejBusLocations(): Job {
        return viewModelScope.launch {
            Log.d(className, "Request for autotrolej bus location")
            while(isActive) {
                try {
                    val data = AutotrolejApi.retrofitService.getCurrentBusLocations()
                    _busLocation.value = formatBusLocationResponse(data)

                    val busLocationItem = _busLocation.value!!.first()
                    Log.d("Iz getAutotorlejBusLocation", busLocationItem.toString())
                    //val lines = scheduleLineDatabaseDao.getLineByStart(busLocationItem.startId)

                    Log.d(className, "DONE")
                } catch (e: Exception) {
                    Log.e(className, "FAIL")
                }
                delay(3000);
            }
        }
    }

    fun getLiveStations(lineVariantId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            liveStations.postValue(lineDatabaseDao.getStations(lineVariantId))
        }

    }

    companion object {
        const val className = "LineVariantModel"
    }


}