package com.uniandes.project.abcall.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.project.abcall.config.ApiResult
import com.uniandes.project.abcall.models.Incident
import com.uniandes.project.abcall.repositories.rest.IncidentRepository
import kotlinx.coroutines.launch

class IncidentsListViewModel(private val repository: IncidentRepository) : ViewModel() {

    private val _incidentes = MutableLiveData<ApiResult<List<Incident>>>()
    val incidentes: LiveData<ApiResult<List<Incident>>> get() = _incidentes

    fun getIncidentsByPerson(id: Int) {
        try {
            repository.getIncidentsByPerson(id) { result ->
                _incidentes.value = result }
        } catch (e: Exception) {
            Log.e("IncidenteViewModel", "Error fetching incidents", e)
        }
    }
}
