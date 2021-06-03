package com.example.autotrolejapp.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.autotrolejapp.R
import com.example.autotrolejapp.entities.Station
import com.example.autotrolejapp.helpers.filterStationsByNameAndGeoXGeoY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class MapFragment : Fragment(){
    private lateinit var mMap: GoogleMap
    private var mapReady = false

    private val viewModel: MapViewModel by lazy {
        ViewModelProvider(this).get(MapViewModel::class.java)
    }

    private val stations: List<Station>
        get() = viewModel.stations.value.orEmpty()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.fragment_map, container, false)
        val mapFragment =  childFragmentManager.findFragmentById(R.id.googleMap) as SupportMapFragment
        val rijekaLocation = LatLng(45.32,14.44)
        mapFragment.getMapAsync {
            googleMap -> mMap = googleMap
            mapReady = true
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rijekaLocation, 11f))
            updateMap()
        }
        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.stations.observe(viewLifecycleOwner, Observer {
            it?.let{
                updateStations()
                updateMap()
            }
        })
    }

    private fun updateStations() {
        //TODO: tu izfiltrirat samo stanice koje se nalaze na trazenoj liniji
        /*Log.d("Iz updateStatiosn >>> ", this.stations.toString())
        if(!stations.isNullOrEmpty()){
            filterStationsByNameAndGeoXGeoY(this.stations)
        }*/
    }

    private fun updateMap(){
        val rijekaLocation = LatLng(45.32,14.44)
        if (mapReady && !stations.isNullOrEmpty()) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rijekaLocation, 11f))
            //Cisto da ne ispisuje sve
            var filteredStations: List<Station> = stations.take(5);
            Log.d("IZ UPDATE MAP", "POSTAVI PINOVE NA MAPU")
            filteredStations.forEach { station ->
                val marker_pos = LatLng(
                    station.location.latitude!!.toDouble(),
                    station.location.longitude!!.toDouble()
                )
                val marker_name = station.name
                mMap.addMarker(
                    MarkerOptions()
                        .position(marker_pos)
                        .title(marker_name)
                        .icon(bitMapFromVector(R.drawable.ic_bus_stop))
                )
                //val cameraPostion = CameraPosition.Builder().target(rijekaLocation).build()
                //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPostion))
            }
        }
    }

    private fun bitMapFromVector(vectorResID:Int):BitmapDescriptor {
        val vectorDrawable=ContextCompat.getDrawable(requireContext(),vectorResID)
        vectorDrawable!!.setBounds(0,0,vectorDrawable!!.intrinsicWidth,vectorDrawable.intrinsicHeight)
        val bitmap=Bitmap.createBitmap(vectorDrawable.intrinsicWidth,vectorDrawable.intrinsicHeight,Bitmap.Config.ARGB_8888)
        val canvas=Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
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