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
import com.uniandes.project.abcall.config.JwtManager
import com.uniandes.project.abcall.config.PreferencesManager
import com.uniandes.project.abcall.config.RetrofitClient
import com.uniandes.project.abcall.databinding.ActivityDashboardBinding
import com.uniandes.project.abcall.databinding.FragmentHomeBinding
import com.uniandes.project.abcall.enums.UserType
import com.uniandes.project.abcall.getCustomSharedPreferences
import com.uniandes.project.abcall.models.Principal
import com.uniandes.project.abcall.ui.CrossIntentActivity
import com.uniandes.project.abcall.ui.LoginActivity
import com.uniandes.project.abcall.ui.dashboard.fragments.DashboardFragment
import com.uniandes.project.abcall.ui.dashboard.fragments.DetailIncidentFragment
import com.uniandes.project.abcall.ui.dashboard.fragments.IncidenceCreateChatbotFragment
import com.uniandes.project.abcall.ui.dashboard.fragments.CrateIncidencesFragment
import com.uniandes.project.abcall.ui.dashboard.fragments.IncidencesFragment
import com.uniandes.project.abcall.ui.dashboard.fragments.MenuFragment
import com.uniandes.project.abcall.ui.dashboard.fragments.MonitorFragment
import com.uniandes.project.abcall.ui.dashboard.fragments.ReportFragment
import com.uniandes.project.abcall.ui.dashboard.intefaces.FragmentChangeListener
import com.uniandes.project.abcall.ui.dashboard.ui.home.HomeFragment

class DashboardActivity : CrossIntentActivity(), FragmentChangeListener {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var jwtManager: JwtManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val sPreferences = getCustomSharedPreferences(binding.root.context)
        preferencesManager = PreferencesManager(sPreferences)
        sharedPreferences = sPreferences

        if (preferencesManager.getToken() == null) {
            preferencesManager.saveToken(intent.getStringExtra("token")!!)
        }


        val token = preferencesManager.getToken()!!
        onlyTest(token)

        RetrofitClient.updateAuthToken(token)

        val principal = preferencesManager.getAuth()

        principal.let {
            if (principal.userType == UserType.USER) {
                changeFragment(IncidencesFragment.newInstance())
            } else{
                changeFragment(MonitorFragment.newInstance())
            }

            supportFragmentManager.addOnBackStackChangedListener {
                val fragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
                if (fragment != null) {
                    updateToolbarTitle(fragment)
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
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
                        if (currentFragment is MonitorFragment) {
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
        if(title == MonitorFragment.TITLE) {
            return false
        }
        return true
    }

    private fun changeFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        updateToolbarTitle(fragment)
    }

    override fun onFragmentChange(fragment: Fragment) {
        changeFragment(fragment)
    }

    private fun updateToolbarTitle(fragment: Fragment) {
        Log.d("DashboardActivity", "Current Fragment: ${fragment::class.java.simpleName}")
        title = when (fragment) {
            is MenuFragment -> MenuFragment.TITLE
            is IncidencesFragment -> IncidencesFragment.TITLE
            is IncidenceCreateChatbotFragment -> IncidenceCreateChatbotFragment.TITLE
            is CrateIncidencesFragment -> CrateIncidencesFragment.TITLE
            is DetailIncidentFragment -> DetailIncidentFragment.TITLE
            is MonitorFragment -> MonitorFragment.TITLE
            else -> "ABCAll App"
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

    fun onlyTest(token: String){
        jwtManager = JwtManager()
        val claims = jwtManager.decodeJWT(token)
        val principal = Principal(
            id = claims["id"] as Int,
            idCompany = claims["id_company"] as Int,
            idPerson = claims["id_person"] as Int?,
            userType = UserType.fromString(claims["user_type"] as String)
        )

        preferencesManager.savePrincipal(principal)
    }
}