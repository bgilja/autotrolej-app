package com.example.autotrolejapp.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autotrolejapp.entities.BusLocation
import com.example.autotrolejapp.entities.Station
import com.example.autotrolejapp.network.AutotrolejApi
import com.example.autotrolejapp.network.formatBusLocationResponse
import com.example.autotrolejapp.network.formatStationResponse
import kotlinx.coroutines.launch

enum class AutotrolejApiStatus { LOADING, ERROR, DONE }

class MapViewModel : ViewModel(){

    private val _status = MutableLiveData<AutotrolejApiStatus>()
    val status: LiveData<AutotrolejApiStatus>
        get() = _status

    private val _stations = MutableLiveData<List<Station>>()
    val stations: LiveData<List<Station>>
        get() = _stations

    private val _busLocation = MutableLiveData<List<BusLocation>>()
    val busLocation: LiveData<List<BusLocation>>
        get() = _busLocation

    init {
        _stations.value = ArrayList()
        _busLocation.value = ArrayList()

        getAutotrolejBusStations()
        //TODO: use BusLocation later whene we will have a line with stations
        //getAutotrolejBusLocations()
    }

    private fun getAutotrolejBusStations() {
        viewModelScope.launch {
            _status.value = AutotrolejApiStatus.LOADING
            Log.d(MapViewModel.className, "Request for autotrolej stations")
            try {
                val data = AutotrolejApi.retrofitService.getStations()
                _stations.value = formatStationResponse(data)
                _status.value = AutotrolejApiStatus.DONE
                Log.d(MapViewModel.className, "DONE")
                //Log.d("IZ VIEWMODELA >>>>>>>>>", _stations.value.toString())
            } catch (e: Exception) {
                _status.value = AutotrolejApiStatus.ERROR
                Log.e(MapViewModel.className, "FAIL")
            }
        }
    }

    private fun getAutotrolejBusLocations() {
        viewModelScope.launch {
            _status.value = AutotrolejApiStatus.LOADING
            Log.d(MapViewModel.className, "Request for autotrolej bus location")
            try {
                val data = AutotrolejApi.retrofitService.getCurrentBusLocations()
                _busLocation.value = formatBusLocationResponse(data)
                _status.value = AutotrolejApiStatus.DONE
                Log.d(MapViewModel.className, "DONE")
                //Log.d(">>>>>>>>>", _busLocation.value.toString())
            } catch (e: Exception) {
                _status.value = AutotrolejApiStatus.ERROR
                Log.e(MapViewModel.className, "FAIL")
            }
        }
    }

    companion object {
        const val className = "MapViewModel"
    }


}