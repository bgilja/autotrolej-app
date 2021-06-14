package com.example.autotrolejapp

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.autotrolejapp.helpers.LocationHelper
import com.example.autotrolejapp.home.HomeFragment
import com.example.autotrolejapp.line_variant.LineVariantFragment
import com.example.autotrolejapp.map.MapFragment

class MainActivity : AppCompatActivity() {

    var activeFragment = HomeFragment.FRAGMENT_ID

    companion object {
        private const val ID_HOME = HomeFragment.FRAGMENT_ID
        private const val ID_MAP = MapFragment.FRAGMENT_ID
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        }

        val fragment = supportFragmentManager.fragments.last()

        val bottomNavigation = findViewById<MeowBottomNavigation>(R.id.bottomNavigation)
        if (fragment is HomeFragment) {
            bottomNavigation.show(ID_HOME)
        } else if (fragment is MapFragment) {
            bottomNavigation.show(ID_MAP)
        }

        if (fragment is LineVariantFragment) {
            val editor = applicationContext.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
            val lineVariantIds = editor.getStringSet("selectedLineVariants", emptySet()).orEmpty().toList()
            val lineNumber = editor.getString("selectedLineNumber", "").orEmpty()

            supportFragmentManager.popBackStack()

            val newFragment: LineVariantFragment = LineVariantFragment.newInstance(lineVariantIds, lineNumber)
            replaceFragment(newFragment)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<MeowBottomNavigation>(R.id.bottomNavigation)
        bottomNavigation.add(MeowBottomNavigation.Model(ID_HOME, R.drawable.ic_baseline_directions_bus_24))
        bottomNavigation.add(MeowBottomNavigation.Model(ID_MAP, R.drawable.ic_baseline_map_24))

        bottomNavigation.show(ID_HOME)
        replaceFragment(HomeFragment.newInstance())

        bottomNavigation.setOnClickMenuListener {
            activateFragment(it.id)
        }

        if (!LocationHelper.checkLocationPermission(applicationContext)) {
            LocationHelper.requestLocationPermission(this)
        }
    }

    private fun activateFragment(fragmentId: Int) {
        activeFragment = fragmentId
        when(fragmentId) {
            ID_HOME -> {
                replaceFragment(HomeFragment.newInstance())
            }
            ID_MAP -> {
                replaceFragment(MapFragment.newInstance())
            }
        }
    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.fragmentContainer, fragment).addToBackStack(Fragment::class.java.simpleName).commit()
    }

}