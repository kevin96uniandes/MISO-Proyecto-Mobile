package com.uniandes.project.abcall.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.databinding.ActivityClientHomeBinding
// import com.uniandes.project.abcall.config.TokenManager

import com.uniandes.project.abcall.databinding.ActivityUserRegisterBinding
import com.uniandes.project.abcall.ui.dashboard.DashboardActivity

// import com.uniandes.project.abcall.ui.dialogs.CustomDialogFragment




class ClientHomeActivity : CrossIntentActivity() {


    private lateinit var btnIncidences: Button
    private lateinit var btnDashboard: Button
    private lateinit var btnReports: Button

    private lateinit var binding: ActivityClientHomeBinding

    // private lateinit var tokenManager: TokenManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityClientHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        btnIncidences = findViewById(R.id.btn_incidences)
        btnIncidences.setOnClickListener {
            val intent = Intent(this, ClientCreateIncidencesActivity::class.java)
            startActivity(intent)
        }

        btnDashboard = findViewById(R.id.btn_dashboard)
        btnDashboard.setOnClickListener({
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        })

        btnReports = findViewById(R.id.btn_reports)

        btnReports.setOnClickListener({

        })

    }


}