package com.uniandes.project.abcall.ui.dashboard.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.uniandes.project.abcall.databinding.FragmentIncidencesBinding
import com.uniandes.project.abcall.ui.dashboard.intefaces.FragmentChangeListener


class DetailIncidentFragment : Fragment() {

    private var _binding: FragmentIncidencesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentChangeListener) {
            var fragmentChangeListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncidencesBinding.inflate(inflater, container, false)
        val incedentId = arguments?.getInt("idIncident", 1) ?: 1
        Log.d("Boton", "ID: ${incedentId}")
        return binding.root
    }
    companion object {
        lateinit var arguments: Bundle
        const val TITLE = "Detalle incidencia"
        @JvmStatic
        fun newInstance() =
            DetailIncidentFragment()
    }
}