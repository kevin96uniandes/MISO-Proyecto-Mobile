package com.uniandes.project.abcall.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uniandes.project.abcall.config.ApiResult
import com.uniandes.project.abcall.models.Incidence
import com.uniandes.project.abcall.repositories.rest.IncidenceClient

class CreateIncidenceViewModel : ViewModel(){

    private val incidenceClient = IncidenceClient()
    private val _result = MutableLiveData<ApiResult<IncidenceClient.IncidenceResponse>>()
    val result: LiveData<ApiResult<IncidenceClient.IncidenceResponse>> get() = _result

    fun createIncidence(incidence: Incidence) {

        incidenceClient.createIncidence(
            IncidenceClient.CreateIncidenceRequest(
                personId = incidence.personId,
                type = incidence.type,
                subject = incidence.subject,
                detail = incidence.detail,
                files = incidence.files
            )
        ){ code ->
            _result.value = code
        }
    }
}