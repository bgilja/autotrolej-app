package com.example.autotrolejapp.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.autotrolejapp.MainActivity
import com.example.autotrolejapp.R
import com.example.autotrolejapp.database.AutotrolejDatabase
import com.example.autotrolejapp.entities.Line
import com.example.autotrolejapp.helpers.filterLinesByArea
import com.example.autotrolejapp.helpers.getDistinctLinesByLineNumber
import com.example.autotrolejapp.line_variant.LineVariantFragment
import com.example.autotrolejapp.pdfview.PdfViewFragment
import com.google.android.material.tabs.TabLayout


class HomeFragment : Fragment() {

    private val adapter = LinesAdapter(object: LinesAdapter.ViewHolder.Listener{
        override fun onScheduleClick(lineNumber: String) {
            val fragment: PdfViewFragment? = PdfViewFragment.newInstance(lineNumber)
            if (fragment != null) {
                (activity as MainActivity).replaceFragment(fragment)
            }
        }

        override fun onRouteClick(lineNumber: String) {
            val linesBylineNumber = lines.filter {x -> x.containsLineNumber(lineNumber)}

            val lineVariantIds = linesBylineNumber.map{ x -> x.variantId }
            val fragment: LineVariantFragment? = LineVariantFragment.newInstance(lineVariantIds, lineNumber)
            if (fragment != null) {
                (activity as MainActivity).replaceFragment(fragment)
            }

        }
    })
    private val lines: List<Line>
        get() = viewModel.lines.value.orEmpty()

    private val viewModel: HomeViewModel by lazy {
        val application = requireNotNull(this.activity).application
        val lineDatabaseDao = AutotrolejDatabase.getInstance(application).lineDatabaseDao
        val viewModelFactory = HomeViewModelFactory(lineDatabaseDao, application)
        ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onResume() {
        super.onResume()

        val tabLayout = view?.findViewById<TabLayout>(R.id.linesTabLayout)
        tabLayout?.getTabAt(1)?.select();
        tabLayout?.getTabAt(0)?.select();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<TabLayout>(R.id.linesTabLayout)

        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                activeFragment = tabLayout.selectedTabPosition + 1
                updateList()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })

        val recyclerView: RecyclerView = view.findViewById(R.id.lineList)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = adapter

        viewModel.lines.observe(viewLifecycleOwner, {
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
        val recyclerView: RecyclerView = requireView().findViewById(R.id.lineList)
        val textNoLines: RelativeLayout = requireView().findViewById(R.id.no_lines)

        var items = getDistinctLinesByLineNumber(filterLinesByArea(this.lines, getActiveArea()))
        items = items.sortedBy { x -> x.displayOrder }

        if(items.isNotEmpty()) {
            recyclerView.visibility = View.VISIBLE
            textNoLines.visibility = View.GONE
        } else {
            recyclerView.visibility = View.GONE
            textNoLines.visibility = View.VISIBLE
        }
        this.adapter.data = items
    }

    companion object {
        const val FRAGMENT_ID: Int = 1
        private var activeFragment = 1

        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}
