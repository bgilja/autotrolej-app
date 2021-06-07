package com.example.autotrolejapp.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.autotrolejapp.R
import com.example.autotrolejapp.database.AutotrolejDatabase
import com.example.autotrolejapp.entities.Line
import com.example.autotrolejapp.helpers.filterLinesByArea
import com.example.autotrolejapp.helpers.getDistinctLinesByLineNumber
import com.google.android.material.tabs.TabLayout


class HomeFragment : Fragment() {

    private val adapter = LinesAdapter()
    private val lines: List<Line>
        get() = viewModel.lines.value.orEmpty()

    private val viewModel: HomeViewModel by lazy {
        val application = requireNotNull(this.activity).application
        val lineDatabaseDao = AutotrolejDatabase.getInstance(application).lineDatabaseDao
        val stationDatabaseDao = AutotrolejDatabase.getInstance(application).stationDatabaseDao
        val lineStationDatabaseDao = AutotrolejDatabase.getInstance(application).lineStationDatabaseDao
        val viewModelFactory = HomeViewModelFactory(lineDatabaseDao, stationDatabaseDao, lineStationDatabaseDao, application)
        ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.lines.observe(viewLifecycleOwner, Observer {
            Log.d("LINES", it.size.toString())
        })

        viewModel.lineStations.observe(viewLifecycleOwner, Observer {
            Log.d("LINE STATIONS", it.size.toString())
        })

        viewModel.stations.observe(viewLifecycleOwner, Observer {
            Log.d("STATIONS", it.size.toString())
        })

        val tabLayout = view.findViewById<TabLayout>(R.id.linesTabLayout)

        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                activeFragment = tabLayout.selectedTabPosition + 1
                Toast.makeText(view.context, activeFragment.toString(), Toast.LENGTH_SHORT).show()
                updateList()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })

        val recyclerView: RecyclerView = view.findViewById(R.id.lineList)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = adapter

        viewModel.lines.observe(viewLifecycleOwner, Observer {
            it?.let{
                updateList()
            }
        })
    }
    private fun getActiveArea(): String {
        return when(activeFragment) {
            2 -> "Wide"
            3 -> "Night"
            else -> "Local"
        }
    }

    private fun updateList() {
        val items = getDistinctLinesByLineNumber(filterLinesByArea(this.lines, getActiveArea()))
        this.adapter.data = items
    }

    companion object {
        const val FRAGMENT_ID: Int = 1

        private var lines = emptyList<Line>()
        private var activeFragment = 1

        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}