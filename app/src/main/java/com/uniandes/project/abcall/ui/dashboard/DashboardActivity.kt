package com.uniandes.project.abcall.ui.dashboard

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.config.PreferencesManager
import com.uniandes.project.abcall.config.RetrofitClient
import com.uniandes.project.abcall.databinding.ActivityDashboardBinding
import com.uniandes.project.abcall.databinding.FragmentHomeBinding
import com.uniandes.project.abcall.enums.UserType
import com.uniandes.project.abcall.ui.CrossIntentActivity
import com.uniandes.project.abcall.ui.LoginActivity
import com.uniandes.project.abcall.ui.dashboard.fragments.DashboardFragment
import com.uniandes.project.abcall.ui.dashboard.fragments.DetailIncidentFragment
import com.uniandes.project.abcall.ui.dashboard.fragments.IncidenceCreateChatbotFragment
import com.uniandes.project.abcall.ui.dashboard.fragments.CrateIncidencesFragment
import com.uniandes.project.abcall.ui.dashboard.fragments.IncidencesFragment
import com.uniandes.project.abcall.ui.dashboard.fragments.MenuFragment
import com.uniandes.project.abcall.ui.dashboard.fragments.ReportFragment
import com.uniandes.project.abcall.ui.dashboard.intefaces.FragmentChangeListener
import com.uniandes.project.abcall.ui.dashboard.ui.home.HomeFragment

class DashboardActivity : CrossIntentActivity(), FragmentChangeListener {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        preferencesManager = PreferencesManager(binding.root.context)
        sharedPreferences = preferencesManager.sharedPreferences
        val token = preferencesManager.getToken()
        if (token == null){
            logout()
        }
        RetrofitClient.updateAuthToken(token!!)

        val principal = preferencesManager.getAuth()

        principal.let {
            if (principal.userType == UserType.USER) {
                changeFragment(IncidencesFragment.newInstance())
            } else{
                changeFragment(MenuFragment.newInstance())
            }

            supportFragmentManager.addOnBackStackChangedListener {
                val fragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
                if (fragment != null) {
                    updateToolbarTitle(fragment)
                    if (fragment is MenuFragment) {
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    } else {
                        supportActionBar?.setDisplayHomeAsUpEnabled(true)
                        if (principal.userType == UserType.USER) {
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                        }
                    }
                }
            }

            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout)

                    if (principal.userType == UserType.USER) {
                        if (currentFragment is IncidencesFragment) {
                            finish()
                        }else{
                            supportFragmentManager.popBackStack()
                        }
                    }else {
                        if (currentFragment is MenuFragment) {
                            finish()
                        } else {
                            supportFragmentManager.popBackStack()
                        }
                    }

                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun changeFragment(fragment: Fragment) {
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

    override fun onFragmentChange(fragment: Fragment) {
        changeFragment(fragment)
    }

    private fun updateToolbarTitle(fragment: Fragment) {
        Log.d("DashboardActivity", "Current Fragment: ${fragment::class.java.simpleName}")
        when (fragment) {
            is MenuFragment -> title = MenuFragment.TITLE
            is IncidencesFragment -> title = IncidencesFragment.TITLE
            is DashboardFragment -> title = DashboardFragment.TITLE
            is ReportFragment -> title = ReportFragment.TITLE
            is IncidenceCreateChatbotFragment -> title = IncidenceCreateChatbotFragment.TITLE
            is CrateIncidencesFragment -> title = CrateIncidencesFragment.TITLE
            is DetailIncidentFragment -> title = DetailIncidentFragment.TITLE
            else -> title = "ABCAll App"
        }
    }

    fun logout() {
        preferencesManager.deletePrincipal()
        preferencesManager.deleteToken()
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", false)
            apply()
        }
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}