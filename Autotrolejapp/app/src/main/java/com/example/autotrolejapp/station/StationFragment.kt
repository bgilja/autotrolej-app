package com.example.autotrolejapp.station

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.autotrolejapp.R
import com.example.autotrolejapp.database.AutotrolejDatabase
import com.example.autotrolejapp.entities.ScheduleStation


private const val ARG_PARAM1 = "param1"

class StationFragment : Fragment() {
    private var stationIdentity: Long = -1

    private val adapter = StationsAdapter()

    private val viewModel: StationViewModel by lazy {
        val application = requireNotNull(this.activity).application
        val stationDatabaseDao = AutotrolejDatabase.getInstance(application).stationDatabaseDao
        val scheduleStationDatabaseDao = AutotrolejDatabase.getInstance(application).scheduleStationDatabaseDao

        val viewModelFactory = StationViewModelFactory(stationDatabaseDao, scheduleStationDatabaseDao, application)
        ViewModelProvider(this, viewModelFactory).get(StationViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            stationIdentity = it.getLong(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_station, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.stationScheduleList)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = adapter

        viewModel.getStation(stationIdentity)
        viewModel.station.observe(viewLifecycleOwner, {
            viewModel.getScheduleStations(it.id)
        })

        viewModel.scheduleStations.observe(viewLifecycleOwner, {
            if (it != null) {
                updateScheduleStations(it)
            }
        })
    }

    private fun updateScheduleStations(listOfScheduledStations: List<ScheduleStation>) {
        val recyclerView: RecyclerView = requireView().findViewById(R.id.stationScheduleList)
        val noScheduledStations: RelativeLayout = requireView().findViewById(R.id.no_scheduleStations)


        if(listOfScheduledStations.isNotEmpty()) {
            recyclerView.visibility = View.VISIBLE
            noScheduledStations.visibility = View.GONE
        } else {
            recyclerView.visibility = View.GONE
            noScheduledStations.visibility = View.VISIBLE
        }
        //TODO: trenutno prosljedujemo ScheduleStation trebali bi liniju bas ili barem jos i liniju dodat
        this.adapter.data = listOfScheduledStations
    }


    companion object {

        @JvmStatic
        fun newInstance(stationIdentity: Long) =
            StationFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PARAM1, stationIdentity)
                }
            }
    }
}