package com.uniandes.project.abcall.ui.dashboard.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uniandes.project.abcall.R


class CreateIncidencesFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_incidences, container, false)
    }

    companion object {
        const val TITLE = "Crear incidencias"
        @JvmStatic
        fun newInstance() =
            CreateIncidencesFragment()
    }
}