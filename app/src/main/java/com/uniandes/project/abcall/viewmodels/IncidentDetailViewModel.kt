package com.uniandes.project.abcall.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uniandes.project.abcall.config.ApiResult
import com.uniandes.project.abcall.models.Incident
import com.uniandes.project.abcall.repositories.rest.IncidentRepository

class IncidentDetailViewModel(private val repository: IncidentRepository) : ViewModel() {

    private val _incidentDetail = MutableLiveData<ApiResult<Incident>>()
    val incidentDetail: LiveData<ApiResult<Incident>> get() = _incidentDetail

    fun findIncidentById(id: Int) {
        try {
            repository.findIncidentById(id) { result ->
                _incidentDetail.value = result }
        } catch (e: Exception) {
            Log.e("IncidentDetailViewModel", "Error fetching incidents", e)
        }
    }
}
