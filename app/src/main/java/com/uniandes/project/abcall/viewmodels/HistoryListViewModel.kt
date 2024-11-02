package com.uniandes.project.abcall.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.project.abcall.config.ApiResult
import com.uniandes.project.abcall.models.History
import com.uniandes.project.abcall.models.Incident
import com.uniandes.project.abcall.repositories.rest.IncidentRepository
import kotlinx.coroutines.launch

class HistoryListViewModel(private val repository: IncidentRepository) : ViewModel() {

    private val _historias = MutableLiveData<ApiResult<List<History>>>()
    val historias: LiveData<ApiResult<List<History>>> get() = _historias

    fun findHistoryByIncident(id: Int) {
        try {
            repository.findHistoryByIncident(id) { result ->
                _historias.value = result }
        } catch (e: Exception) {
            Log.e("HistoryListViewModel", "Error fetching incidents", e)
        }
    }
}
