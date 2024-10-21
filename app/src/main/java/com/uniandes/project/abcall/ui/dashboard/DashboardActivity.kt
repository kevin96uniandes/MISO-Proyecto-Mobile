package com.uniandes.project.abcall.ui.dashboard

import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.databinding.ActivityDashboardBinding
import com.uniandes.project.abcall.databinding.FragmentHomeBinding
import com.uniandes.project.abcall.ui.dashboard.fragments.DashboardFragment
import com.uniandes.project.abcall.ui.dashboard.fragments.IncidencesFragment
import com.uniandes.project.abcall.ui.dashboard.fragments.MenuFragment
import com.uniandes.project.abcall.ui.dashboard.fragments.ReportFragment
import com.uniandes.project.abcall.ui.dashboard.intefaces.FragmentChangeListener
import com.uniandes.project.abcall.ui.dashboard.ui.home.HomeFragment

class DashboardActivity : AppCompatActivity(), FragmentChangeListener {

    private lateinit var binding: ActivityDashboardBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        changeFragment(MenuFragment.newInstance(), MenuFragment.TITLE)

        supportFragmentManager.addOnBackStackChangedListener {
            val fragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
            if (fragment != null) {
                updateToolbarTitle(fragment)
                if (fragment is MenuFragment) {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                } else {
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout)

                if (currentFragment is MenuFragment) {
                    finish()
                } else {
                    supportFragmentManager.popBackStack()
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun changeFragment(fragment: Fragment, title: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        if (fragment is MenuFragment) {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        updateToolbarTitle(fragment)
    }

    override fun onFragmentChange(fragment: Fragment, title: String) {
        changeFragment(fragment, title)
    }

    private fun updateToolbarTitle(fragment: Fragment) {
        Log.d("DashboardActivity", "Current Fragment: ${fragment::class.java.simpleName}")
        when (fragment) {
            is MenuFragment -> setTitle(MenuFragment.TITLE)
            is IncidencesFragment -> setTitle(IncidencesFragment.TITLE)
            is DashboardFragment -> setTitle(DashboardFragment.TITLE)
            is ReportFragment -> setTitle(ReportFragment.TITLE)
            else -> setTitle("ABCAll App") // Un título por defecto
        }
    }
}