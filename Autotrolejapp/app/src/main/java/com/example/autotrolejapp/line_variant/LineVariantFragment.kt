package com.example.autotrolejapp.line_variant

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.autotrolejapp.R
import com.example.autotrolejapp.database.AutotrolejDatabase
import com.example.autotrolejapp.entities.BusLocation
import com.example.autotrolejapp.entities.Station
import com.example.autotrolejapp.helpers.LocationHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext


class LineVariantFragment : Fragment() {
    private var _lineVariantIds = emptyList<String>()
    private var mapOfLineVariants = HashMap<String, String>()
    var lineVariantIds: List<String>
        get() {
            return _lineVariantIds
        }
        set(value) {
            _lineVariantIds = value

            viewModel.getBusForLine(value)
            viewModel.getLineStations(value)
        }
    private lateinit var lineVariantsChanged: MutableList<String>
    private lateinit var mMap: GoogleMap
    private var busLocationMarkers: MutableList<Marker> = mutableListOf()
    private var stationMarkers: MutableList<Marker> = mutableListOf()
    private var mapReady = false
    private var setCamera = false
    private lateinit var chipGroup: ChipGroup

    val updateBusLocationsScope = CoroutineScope(newSingleThreadContext("update_bus_locations"))
    var doUpdateBusLocation = false

    protected var currentLocationMarkers: MutableList<Marker> = mutableListOf()
    protected var currentLocation: Location? = null

    private lateinit var locationClient: FusedLocationProviderClient
    private val mLocationCallback: LocationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult) {
            val locations = locationResult.locations.filter { location -> location != null }
            Log.d("LOCATION UPDATED", locations.size.toString())

            for (location in locations) {
                if (location != null) {
                    currentLocation = location
                }
            }
        }

    }

    private val viewModel: LineVariantViewModel by lazy {
        val application = requireNotNull(this.activity).application
        val lineDatabaseDao = AutotrolejDatabase.getInstance(application).lineDatabaseDao
        val scheduleLineDatabaseDao = AutotrolejDatabase.getInstance(application).scheduleLineDatabaseDao
        val viewModelFactory = LineVariantViewModelFactory(lineDatabaseDao, scheduleLineDatabaseDao, application)
        ViewModelProvider(this, viewModelFactory).get(LineVariantViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_line_variant, container, false)
        readBundle(arguments)

        chipGroup = view.findViewById(R.id.chip_group)

        val mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        val rijekaLocation = LatLng(45.32,14.44)
        mapFragment.getMapAsync {
                googleMap -> mMap = googleMap
            mapReady = true
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rijekaLocation, 11f))
        }

        return view
    }

    private fun setCurrentLocation() {
        currentLocationMarkers.forEach{ marker -> marker.remove() }
        locationClient = LocationHelper.getCurrentLocation(requireActivity(), requireContext(), mLocationCallback, 5000)
    }

    override fun onPause() {
        super.onPause()

        locationClient.removeLocationUpdates(mLocationCallback)

        //TODO: probat sutra novu animiaciju kamere i dal mice bus stvarno sa kad se makne linije
        busLocationMarkers.forEach { marker ->
            marker.remove()
        }

        this.doUpdateBusLocation = false
    }

    override fun onResume() {
        super.onResume()

        setCurrentLocation()

        this.doUpdateBusLocation = true

        this.updateBusLocationsScope.launch {
            updateBusLocations()
        }
    }

    private fun readBundle(bundle: Bundle?) {
        if (bundle != null) {
            this.lineVariantIds = bundle.getStringArrayList("lineVariantsIds") as ArrayList<String>
            lineVariantsChanged = this.lineVariantIds as ArrayList<String>

            viewModel.getFilteredLinesbylineVariant(lineVariantIds)

            viewModel.filteredLines.observe(viewLifecycleOwner, { filteredLines ->
                filteredLines.forEach { line ->
                    mapOfLineVariants.put(line.variantId, line.variantName)
                }

                setChipsDynamicly(chipGroup)
            })

            viewModel.selectedBusLocations.observe(viewLifecycleOwner, {
                updateMapBusLocation(it)
            })

            viewModel.getLineStations(lineVariantIds)
            viewModel.lineStations.observe(viewLifecycleOwner, {
                updateMap(it)
            })
        }
    }

    private fun setChipsDynamicly(chipGroup: ChipGroup?) {
        this.lineVariantIds.forEach() { variant ->
            val mChip = this.layoutInflater.inflate(R.layout.line_variant_chip, chipGroup, false) as Chip
            mChip.text = mapOfLineVariants[variant]
            mChip.contentDescription = variant

            mChip.setOnCheckedChangeListener { compoundButton, b ->
                if(b){
                    if(lineVariantsChanged.contains(compoundButton.contentDescription.toString())) return@setOnCheckedChangeListener
                    lineVariantsChanged.add(compoundButton.contentDescription.toString())
                    mChip.isCloseIconVisible = false
                } else {
                    if(lineVariantsChanged.size == 1) return@setOnCheckedChangeListener
                    lineVariantsChanged = lineVariantsChanged.filter { variantId -> variantId != compoundButton.contentDescription.toString()} as MutableList<String>
                    mChip.isCloseIconVisible = true
                }
                this.lineVariantIds = lineVariantsChanged
            }

            chipGroup?.addView(mChip)
        }
    }

    private suspend fun updateBusLocations() {
        while(doUpdateBusLocation) {
            viewModel.getBusForLine(this.lineVariantIds)
            delay(7000)
        }
    }

    private fun updateMapBusLocation(busLocations: List<BusLocation>) {
        busLocationMarkers.forEach { marker ->
            marker.remove()
        }

        if (mapReady) {
            viewModel.selectedBusLocations.value?.forEach { busLocation ->
                val markerPos = LatLng(
                    busLocation.longitude!!.toDouble(),
                    busLocation.latitude!!.toDouble()
                )
                val markerName = busLocation.busName

                /*busLocationMarkers.add(mMap.addMarker(
                    MarkerOptions()
                        .position(markerPos)
                        .title(markerName)
                        .icon(bitMapFromVector(R.drawable.ic_red_bus))
                ))*/
                val markerlocation= mMap.addMarker(
                    MarkerOptions()
                        .position(markerPos)
                        .title(markerName)
                        .icon(bitMapFromVector(R.drawable.ic_red_bus))
                )
                busLocationMarkers.add(markerlocation)

                Log.d("CURRENT_LOCATION", currentLocation.toString())
                if(viewModel.selectedBusLocations.value!!.size == 1) {
                    animateMarker(markerlocation, markerPos, false)
                    //Log.d("Iz updateMapBusLocation", "Sad animiraj")
                }
            }
        }
    }

    fun animateMarker(
        marker: Marker, toPosition: LatLng,
        hideMarker: Boolean
    ) {
        val handler = Handler()
        val start = SystemClock.uptimeMillis()
        val proj: Projection = mMap.getProjection()
        val startPoint: Point = proj.toScreenLocation(marker.position)
        val startLatLng: LatLng = proj.fromScreenLocation(startPoint)
        val duration: Long = 500
        val interpolator: Interpolator = LinearInterpolator()
        handler.post(object : Runnable {
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - start
                val t: Float = interpolator.getInterpolation(
                    elapsed.toFloat()
                            / duration
                )
                val lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude
                val lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude
                marker.position = LatLng(lat, lng)

                if (mMap != null) mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(lat, lng),
                        15.0f
                    )
                )

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16)
                } else {
                    if (hideMarker) {
                        marker.isVisible = false
                    } else {
                        marker.isVisible = true
                    }
                }
            }
        })
    }


    private fun updateMap(stationsByLineVariant: List<Station>){
        stationMarkers.forEach { marker ->
            marker.remove()
        }

        if (mapReady) {
            if(!setCamera){
                //TODO: mozda setat kameru na lokaciju di si kad bude gps
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getCenterPoint(stationsByLineVariant), 13.5f))
                setCamera = true
            }
            stationsByLineVariant.forEach { station ->
                val markerPos = LatLng(
                    station.latitude!!.toDouble(),
                    station.longitude!!.toDouble()
                )
                val markerName = station.name
                stationMarkers.add(mMap.addMarker(
                    MarkerOptions()
                        .position(markerPos)
                        .title(markerName)
                        .icon(bitMapFromVector(R.drawable.ic_bus_stop))
                ))
            }
        }
    }

    private fun getCenterPoint (points: List<Station>): LatLng? {
        var latitude = 0.0
        var longitude = 0.0
        val n = points.size
        for (point in points) {
            latitude += point.latitude!!
            longitude += point.longitude!!
        }
        return LatLng(latitude / n, longitude / n)
    }

    private fun bitMapFromVector(vectorResID:Int): BitmapDescriptor {
        val vectorDrawable= ContextCompat.getDrawable(requireContext(),vectorResID)
        vectorDrawable!!.setBounds(0,0,vectorDrawable!!.intrinsicWidth,vectorDrawable.intrinsicHeight)
        val bitmap= Bitmap.createBitmap(vectorDrawable.intrinsicWidth,vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888)
        val canvas= Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    companion object {
        @JvmStatic
        fun newInstance(lineVariantIds: List<String>, lineNumber: String) : LineVariantFragment? {
            val bundle = Bundle()
            bundle.putStringArrayList("lineVariantsIds", (ArrayList<String>(lineVariantIds)))
            bundle.putString("lineNumber", lineNumber)
            val fragment = LineVariantFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
