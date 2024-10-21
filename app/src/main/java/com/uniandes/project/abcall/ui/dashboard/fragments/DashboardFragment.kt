package com.uniandes.project.abcall.ui.dashboard.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uniandes.project.abcall.R

class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard2, container, false)
    }

    companion object {
        const val TITLE = "Dashboard"

        @JvmStatic
        fun newInstance() =
            DashboardFragment()
    }
}