package com.uniandes.project.abcall.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uniandes.project.abcall.models.Incidence
import com.uniandes.project.abcall.repositories.rest.IncidenceClient

class CreateIncidenceViewModel(private val context: Context) : ViewModel(){

    private val incidenceClient = IncidenceClient()
    private val _result = MutableLiveData<Int>()
    val result: LiveData<Int> get() = _result

    fun createIncidence(incidence: Incidence) {
        val contentResolver = context.contentResolver

        incidenceClient.createIncidence(
            IncidenceClient.CreateIncidenceRequest(
                personId = incidence.personId,
                type = incidence.type,
                subject = incidence.subject,
                detail = incidence.detail,
                files = incidence.files
            ),
            contentResolver
        ){ inc ->
            _result.value = 0
        }
    }
}