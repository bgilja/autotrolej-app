package com.example.autotrolejapp.line_variant

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
import androidx.lifecycle.ViewModelProvider
import com.example.autotrolejapp.R
import com.example.autotrolejapp.database.AutotrolejDatabase
import com.example.autotrolejapp.entities.BusLocation
import com.example.autotrolejapp.helpers.BaseFragment
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.*
import java.lang.Exception


class LineVariantFragment : BaseFragment() {

    private var _lineVariantIds = emptyList<String>()
    private var mapOfLineVariants = HashMap<String, String>()

    private var lineVariantIds: List<String>
        get() {
            return _lineVariantIds
        }
        set(value) {
            _lineVariantIds = value

            viewModel.getBusForLine(value)
            viewModel.getLineStations(value)
        }

    private lateinit var lineVariantsChanged: MutableList<String>

    private var selectedBusesFirstTime: MutableList<BusLocation> = mutableListOf()
    private var selectedBuses: MutableList<BusLocation> = mutableListOf()
    private var firstTimeFindBuses = false

    private lateinit var chipGroupLineVariants: ChipGroup
    private lateinit var chipGroupBuses: ChipGroup
    private var busChipChecked: String = ""

    private var followLocation = true

    @ObsoleteCoroutinesApi
    val updateBusLocationsScope = CoroutineScope(newSingleThreadContext("update_bus_locations"))
    var doUpdateBusLocation = false

    private var delayTimeInterval: Long = 5000
    private var currentLocation: Location? = null

    override val mLocationCallback: LocationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult) {
            val locations = locationResult.locations.filter { location -> location != null }

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

        chipGroupLineVariants = view.findViewById(R.id.chip_group)
        chipGroupBuses = view.findViewById(R.id.chip_group_buses)

        val mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        initMap(mapFragment)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentLocationChip = view.findViewById<Chip>(R.id.chip_current_location)
        followLocation = currentLocationChip.isChecked

        currentLocationChip.setOnCheckedChangeListener { _, isChecked ->

            val checkedChipId = chipGroupBuses.checkedChipId

            try {
                val checkedChip = chipGroupBuses.findViewById<Chip>(checkedChipId)
                checkedChip.isChecked = false
            } catch (e: Exception) {
                Log.e("MY LOCATION", e.toString())
            }

            currentLocationChip.isChecked = isChecked
            followLocation = isChecked
        }
    }

    override fun onPause() {
        super.onPause()

        locationClient.removeLocationUpdates(mLocationCallback)

        busLocationMarkers.forEach { marker -> marker.remove() }
        this.doUpdateBusLocation = false

        followLocation = false
    }

    @ObsoleteCoroutinesApi
    override fun onResume() {
        super.onResume()

        setCurrentLocation(delayTimeInterval)

        this.doUpdateBusLocation = true
        this.updateBusLocationsScope.launch { updateBusLocations() }
    }

    private fun readBundle(bundle: Bundle?) {
        if (bundle != null) {
            this.lineVariantIds = bundle.getStringArrayList("lineVariantsIds") as ArrayList<String>
            lineVariantsChanged = this.lineVariantIds as ArrayList<String>

            viewModel.getFilteredLinesbylineVariant(lineVariantIds)

            viewModel.filteredLines.observe(viewLifecycleOwner, { filteredLines ->
                filteredLines.forEach { line -> mapOfLineVariants[line.variantId] = line.variantName }
                setLineVariantChipsDynamically(chipGroupLineVariants)
            })

            viewModel.selectedBusLocations.observe(viewLifecycleOwner, {
                selectedBuses = it

                if(!firstTimeFindBuses){
                    selectedBusesFirstTime = selectedBuses
                    firstTimeFindBuses = true
                    setBusChipsDynamically()
                } else {
                    //check if busLocations have changed, if did set new chips on top // ako se u starom popisu buseva ne nalazi neki novi
                    selectedBuses.forEach{ newBus ->
                        if (!(selectedBusesFirstTime.any { x -> x.busName == newBus.busName }) || (selectedBuses.size != selectedBusesFirstTime.size)){
                            selectedBusesFirstTime = selectedBuses
                            //ako u toj novoj listi sad ne postoji taj bus kojeg si trenutno pratio
                            if(!(selectedBusesFirstTime.any{ x -> x.busName == busChipChecked} )) busChipChecked = ""
                            chipGroupBuses.removeViews(1, chipGroupBuses.childCount-1)
                            setBusChipsDynamically()
                        }
                    }
                }

                updateMapBusLocation(it)
            })

            viewModel.getLineStations(lineVariantIds)
            viewModel.lineStations.observe(viewLifecycleOwner, {
                updateMapStations(it)
            })
        }
    }

    private fun setBusChipsDynamically() {
        selectedBusesFirstTime.forEach { bus ->

            val bChip = this.layoutInflater.inflate(R.layout.bus_chip, chipGroupBuses, false) as Chip
            bChip.text = bus.busName

            if(busChipChecked.isNotEmpty() && busChipChecked == bus.busName) {
                bChip.isChecked = true
            }

            bChip.setOnCheckedChangeListener { compoundButton, b ->
                if(b) {
                    busChipChecked = compoundButton.text.toString()
                    bChip.isCloseIconVisible = false
                } else {
                    if(busChipChecked == compoundButton.text.toString()) busChipChecked = ""
                    bChip.isCloseIconVisible = true
                }

                val currentLocationChip = view?.findViewById<Chip>(R.id.chip_current_location)
                currentLocationChip?.isChecked = false
            }

            chipGroupBuses.addView(bChip)
        }

    }

    private fun setLineVariantChipsDynamically(chipGroup: ChipGroup?) {
        this.lineVariantIds.forEach { variant ->

            val mChip = this.layoutInflater.inflate(R.layout.line_variant_chip, chipGroup, false) as Chip
            mChip.text = mapOfLineVariants[variant]
            mChip.contentDescription = variant

            mChip.setOnCheckedChangeListener { compoundButton, b ->
                if(b) {
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
            delay(delayTimeInterval)
        }
    }

    override fun updateMapBusLocation(busLocations: List<BusLocation>) {
        if (followLocation && currentLocation != null) {
            updateCurrentLocation(currentLocation!!, true)
        }

        busLocationMarkers.forEach { marker ->
            marker.remove()
        }

        if (mapReady) {
            busLocations.forEach { busLocation ->
                val markerPos = LatLng(
                    busLocation.latitude!!.toDouble(),
                    busLocation.longitude!!.toDouble()
                )
                val markerName = busLocation.busName

                val markerLocation = mMap.addMarker(
                    MarkerOptions()
                        .position(markerPos)
                        .title(markerName)
                        .icon(bitMapFromVector(R.drawable.ic_red_bus))
                )

                if (markerLocation != null) {
                    busLocationMarkers.add(markerLocation)

                    if(busChipChecked.isNotEmpty()) {
                        if(busLocation.busName == busChipChecked) {
                            animateMarker(markerLocation, markerPos, false)
                        }
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(lineVariantIds: List<String>, lineNumber: String) : LineVariantFragment {
            val bundle = Bundle()

            bundle.putStringArrayList("lineVariantsIds", (ArrayList<String>(lineVariantIds)))
            bundle.putString("lineNumber", lineNumber)

            val fragment = LineVariantFragment()
            fragment.arguments = bundle

            return fragment
        }
    }
}
