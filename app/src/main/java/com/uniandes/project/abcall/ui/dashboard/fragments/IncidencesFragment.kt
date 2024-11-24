package com.uniandes.project.abcall.ui.dashboard.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.adapter.IncidentChatbotAdapter
import com.uniandes.project.abcall.adapter.IncidentsListAdapter
import com.uniandes.project.abcall.config.ApiResult
import com.uniandes.project.abcall.config.PreferencesManager
import com.uniandes.project.abcall.databinding.FragmentIncidencesBinding
import com.uniandes.project.abcall.getCustomSharedPreferences
import com.uniandes.project.abcall.models.ChatbotMessage
import com.uniandes.project.abcall.models.Incident
import com.uniandes.project.abcall.repositories.rest.IncidentRepository
import com.uniandes.project.abcall.ui.dashboard.intefaces.FragmentChangeListener
import com.uniandes.project.abcall.viewmodels.IncidentsListViewModel

class IncidencesFragment : Fragment() {

    private var _binding: FragmentIncidencesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: IncidentsListViewModel
    private val incidentsList = IncidentRepository()
    private lateinit var incidentAdapter: IncidentsListAdapter
    private val incidentList = mutableListOf<Incident>()
    private lateinit var preferencesManager: PreferencesManager

    private var fragmentChangeListener: FragmentChangeListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentChangeListener) {
            fragmentChangeListener = context
            TITLE = context.getString(R.string.issues)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncidencesBinding.inflate(inflater, container, false)
        incidentList.clear()

        val sPreferences = getCustomSharedPreferences(binding.root.context)
        preferencesManager = PreferencesManager(sPreferences)
        val principal = preferencesManager.getAuth()

        viewModel = IncidentsListViewModel(incidentsList)
        viewModel.getIncidentsByPerson(principal.idPerson!!)

        val recyclerView: RecyclerView = binding.incidentRecyclerView
        incidentAdapter = IncidentsListAdapter(incidentList)
        recyclerView.adapter = incidentAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        binding.incidenceMenuItemChatbot.setOnClickListener {
            fragmentChangeListener?.onFragmentChange(IncidenceCreateChatbotFragment.newInstance())
        }

        binding.incidenceMenuItemManual.setOnClickListener{
            fragmentChangeListener?.onFragmentChange(CrateIncidencesFragment.newInstance())
        }

        viewModel.incidentes.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApiResult.Success -> {
                    result.data.map {
                        incidentList.add(it)
                    }
                    incidentAdapter.notifyItemInserted(incidentList.size)
                }
                is ApiResult.Error -> { }
                is ApiResult.NetworkError -> { }
            }
        }
        return binding.root
    }

    companion object {
        var TITLE = ""
        @JvmStatic
        fun newInstance() =
            IncidencesFragment()
    }
}