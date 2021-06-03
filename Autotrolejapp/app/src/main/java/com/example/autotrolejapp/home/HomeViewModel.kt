package com.example.autotrolejapp.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autotrolejapp.entities.Line
import com.example.autotrolejapp.entities.Schedule
import com.example.autotrolejapp.network.AutotrolejApi
import com.example.autotrolejapp.network.formatLineResponse
import com.example.autotrolejapp.network.formatScheduleResponse
import kotlinx.coroutines.launch

enum class AutotrolejApiStatus { LOADING, ERROR, DONE }

class HomeViewModel : ViewModel() {

    private val _status = MutableLiveData<AutotrolejApiStatus>()
    val status: LiveData<AutotrolejApiStatus>
        get() = _status

    private val _lines = MutableLiveData<List<Line>>()
    val lines: LiveData<List<Line>>
                get() = _lines

    private val _currentDaySchedule = MutableLiveData<Schedule>()
    val currentDaySchedule: LiveData<Schedule>
        get() = _currentDaySchedule

    init {
        _lines.value = ArrayList()
        _currentDaySchedule.value = null

        getAutotrolejLines()
        // getAutotrolejScheduleToday()
    }

    private fun getAutotrolejLines() {
        viewModelScope.launch {
            _status.value = AutotrolejApiStatus.LOADING
            Log.d(className, "Request for autotrolej lines")
            try {
                val data = AutotrolejApi.retrofitService.getLines()
                _lines.value = formatLineResponse(data)
                _status.value = AutotrolejApiStatus.DONE
                Log.d(className, "DONE")
            } catch (e: Exception) {
                _status.value = AutotrolejApiStatus.ERROR
                Log.e(className, "FAIL")
            }
        }
    }

    private fun getAutotrolejScheduleToday() {
        viewModelScope.launch {
            _status.value = AutotrolejApiStatus.LOADING
            Log.d(className, "Request for autotrolej lines")
            try {
                val data = AutotrolejApi.retrofitService.getScheduleToday()
                _currentDaySchedule.value = formatScheduleResponse(data)
                _status.value = AutotrolejApiStatus.DONE
                Log.d(className, "DONE")
            } catch (e: Exception) {
                _status.value = AutotrolejApiStatus.ERROR
                Log.e(className, "FAIL")
            }
        }
    }

    companion object {
        const val className = "HomeViewModel"
    }
}