package com.uniandes.project.abcall.ui.dashboard.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.adapter.HistoryListAdapter
import com.uniandes.project.abcall.adapter.IncidentsListAdapter
import com.uniandes.project.abcall.config.ApiResult
import com.uniandes.project.abcall.databinding.FragmentDetailIncidentBinding
import com.uniandes.project.abcall.models.History
import com.uniandes.project.abcall.models.Incident
import com.uniandes.project.abcall.repositories.rest.IncidentRepository
import com.uniandes.project.abcall.ui.dashboard.intefaces.FragmentChangeListener
import com.uniandes.project.abcall.viewmodels.HistoryListViewModel
import com.uniandes.project.abcall.viewmodels.IncidentDetailViewModel
import com.uniandes.project.abcall.viewmodels.IncidentsListViewModel

class DetailIncidentFragment : Fragment() {

    private var _binding: FragmentDetailIncidentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: IncidentDetailViewModel
    private val incidentDetail = IncidentRepository()
    private var incidente: Incident = Incident()
    private lateinit var asuntoTextView: TextView
    private lateinit var tipoTextView: TextView
    private lateinit var estadoTextView: TextView
    private lateinit var estadoView: View
    private lateinit var detalleTextView: TextView

    private lateinit var viewModelHistory: HistoryListViewModel
    private val historiesList = IncidentRepository()
    private lateinit var historyAdapter: HistoryListAdapter
    private val historyList = mutableListOf<History>()

    private var fragmentChangeListener: FragmentChangeListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentChangeListener) {
            var fragmentChangeListener = context
            TITLE = context.getString(R.string.issue_detail)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailIncidentBinding.inflate(inflater, container, false)
        val incedentId = arguments?.getInt("idIncident", 1) ?: 1
        historyList.clear()

        viewModel = IncidentDetailViewModel(incidentDetail)
        viewModel.findIncidentById(incedentId)

        viewModelHistory = HistoryListViewModel(historiesList)
        viewModelHistory.findHistoryByIncident(incedentId)

        asuntoTextView = binding.asuntoTextView
        tipoTextView = binding.tipoTextView
        estadoTextView = binding.estadoTextView
        estadoView = binding.estadoView
        detalleTextView = binding.detalleTextView

        val recyclerView: RecyclerView = binding.procesoRecyclerView
        historyAdapter = HistoryListAdapter(requireContext(), historyList)
        recyclerView.adapter = historyAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.incidentDetail.observe(viewLifecycleOwner){ result ->
            when (result) {
                is ApiResult.Success -> {
                    Log.d("AsuntoFragment", "AsuntoFragment: ${result.data.estadoId}")
                    incidente = result.data
                    asuntoTextView.text = incidente.asunto
                    tipoTextView.text = getTypeName(incidente.tipoId)
                    estadoTextView.text = getStateName(incidente.estadoId)
                    setEstadoBackground(incidente.estadoId)
                    detalleTextView.text = incidente.descripcion
                }
                is ApiResult.Error -> { }
                is ApiResult.NetworkError -> { }
            }
        }

        viewModelHistory.historias.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApiResult.Success -> {
                    result.data.map {
                        historyList.add(it)
                    }
                    historyAdapter.notifyItemInserted(historyList.size)
                }
                is ApiResult.Error -> { }
                is ApiResult.NetworkError -> { }
            }
        }

        return binding.root
    }

    private fun getTypeName(tipoId: Int): String {
        return when (tipoId) {
            1 -> "Petición"
            2 -> "Queja/Reclamo"
            3 -> "Sugerencia"
            else -> "Desconocido"
        }
    }

    private fun getStateName(estadoId: Int): String {
        return when (estadoId) {
            1 -> "Abierto"
            2 -> "Desestimado"
            3 -> "Escalado"
            4 -> "Cerrado Satisfactoriamente"
            5 -> "Cerrado Insatisfactoriamente"
            6 -> "Reaperturado"
            else -> "Desconocido"
        }
    }

    private fun setEstadoBackground(estadoId: Int) {
        val backgroundResource = when (estadoId) {
            1 -> R.drawable.estado_abierto
            2 -> R.drawable.estado_desestimado
            3 -> R.drawable.estado_escalado
            4 -> R.drawable.estado_cerrado_satisfactorio
            5 -> R.drawable.estado_cerrado_insatisfactorio
            6 -> R.drawable.estado_reaperturado
            else -> R.drawable.estado_cerrado_insatisfactorio
        }
        estadoView.setBackgroundResource(backgroundResource)
    }

    companion object {
        lateinit var arguments: Bundle
        var TITLE = ""
        @JvmStatic
        fun newInstance() =
            DetailIncidentFragment()
    }
}