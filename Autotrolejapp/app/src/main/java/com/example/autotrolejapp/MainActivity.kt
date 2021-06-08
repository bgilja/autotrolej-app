package com.example.autotrolejapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.autotrolejapp.home.HomeFragment
import com.example.autotrolejapp.map.MapFragment

class MainActivity : AppCompatActivity() {

    companion object {
        private const val ID_HOME = HomeFragment.FRAGMENT_ID
        private const val ID_MAP = MapFragment.FRAGMENT_ID
        private const val ID_BUS = BusFragment.FRAGMENT_ID
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val fragment = supportFragmentManager.fragments.last()

        val bottomNavigation = findViewById<MeowBottomNavigation>(R.id.bottomNavigation)
        if (fragment is HomeFragment) {
            bottomNavigation.show(ID_HOME)
        } else if (fragment is MapFragment) {
            bottomNavigation.show(ID_MAP)
        } else if (fragment is BusFragment) {
            bottomNavigation.show(ID_BUS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<MeowBottomNavigation>(R.id.bottomNavigation)
        bottomNavigation.add(MeowBottomNavigation.Model(ID_HOME, R.drawable.ic_baseline_home_24))
        bottomNavigation.add(MeowBottomNavigation.Model(ID_MAP, R.drawable.ic_baseline_map_24))
        bottomNavigation.add(MeowBottomNavigation.Model(ID_BUS, R.drawable.ic_baseline_directions_bus_24))

        bottomNavigation.show(ID_HOME)
        replaceFragment(HomeFragment.newInstance())

        bottomNavigation.setOnClickMenuListener {
            activateFragment(it.id)
        }
    }

    private fun activateFragment(fragmentId: Int) {
        when(fragmentId) {
            ID_HOME -> {
                Toast.makeText(this@MainActivity, "Home fragment", Toast.LENGTH_SHORT).show()
                replaceFragment(HomeFragment.newInstance())
            }
            ID_MAP -> {
                Toast.makeText(this@MainActivity, "Map fragment", Toast.LENGTH_SHORT).show()
                replaceFragment(MapFragment.newInstance())
            }
            ID_BUS -> {
                Toast.makeText(this@MainActivity, "Bus fragment", Toast.LENGTH_SHORT).show()
                replaceFragment(BusFragment.newInstance())
            }
        }
    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.fragmentContainer, fragment).addToBackStack(Fragment::class.java.simpleName).commit()
    }

}