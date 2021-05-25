package com.example.autotrolejapp.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.autotrolejapp.R
import com.example.autotrolejapp.entities.Line
import com.example.autotrolejapp.helpers.filterLinesByArea
import com.google.android.material.tabs.TabLayout


class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.lines.observeForever {
            lines = viewModel.lines.value!!
            Log.d(HomeFragment::class.java.name, "Lines changed")
            Log.d(HomeFragment::class.java.name, lines.size.toString())
            updateList()
        }

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    }

    private fun updateList() {
        Log.d("82949023402948903", activeFragment.toString())
        val x = filterLinesByArea(lines, "Local")
        Log.d("302012931289081239031", x.size.toString())

        val usedLines = mutableSetOf<String>()
        for (line in x) {
            if (!usedLines.contains(line.lineNumber)) {
                Log.d("8031280912890231", line.lineNumber + " " + line.variantName)
                usedLines.add(line.lineNumber)
            }
        }
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