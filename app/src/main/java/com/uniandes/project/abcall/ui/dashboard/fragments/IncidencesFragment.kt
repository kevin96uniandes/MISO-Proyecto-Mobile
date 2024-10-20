package com.uniandes.project.abcall.ui.dashboard.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uniandes.project.abcall.R


class IncidencesFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_incidences, container, false)
    }

    companion object {
        const val TITLE = "Incidencias"
        @JvmStatic
        fun newInstance() =
            IncidencesFragment()
    }
}