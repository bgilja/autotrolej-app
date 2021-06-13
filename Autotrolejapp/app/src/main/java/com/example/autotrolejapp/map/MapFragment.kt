package com.example.autotrolejapp.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.autotrolejapp.R
import com.example.autotrolejapp.database.AutotrolejDatabase
import com.example.autotrolejapp.helpers.BaseFragment
import com.google.android.gms.maps.SupportMapFragment


class MapFragment : BaseFragment(){

    private val viewModel: MapViewModel by lazy {
        val application = requireNotNull(this.activity).application
        val stationDatabaseDao = AutotrolejDatabase.getInstance(application).stationDatabaseDao
        val viewModelFactory = MapViewModelFactory(stationDatabaseDao, application)
        ViewModelProvider(this, viewModelFactory).get(MapViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFragment =  childFragmentManager.findFragmentById(R.id.googleMap) as SupportMapFragment
        initMap(mapFragment)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.stations.observe(viewLifecycleOwner, {
            updateMapStations(it)
        })

        viewModel.busLocations.observe(viewLifecycleOwner, {
            updateMapBusLocation(it)
        })

        setCurrentLocation()
    }

    companion object {
        const val FRAGMENT_ID: Int = 2

        @JvmStatic
        fun newInstance() =
            MapFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}