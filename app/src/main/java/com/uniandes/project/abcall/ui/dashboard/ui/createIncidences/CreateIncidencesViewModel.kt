package com.uniandes.project.abcall.ui.dashboard.ui.createIncidences

import androidx.lifecycle.ViewModel
import com.uniandes.project.abcall.repositories.rest.CreateIncidence
import com.uniandes.project.abcall.repositories.rest.RegisterUserClient
import okhttp3.MultipartBody


class CreateIncidencesViewModel (
    private val createIncidenceClient: CreateIncidence
    ) : ViewModel() {


    fun createIncidence(type: Int, subject: String, detail: String, files: List<MultipartBody.Part>) {
        createIncidenceClient.createIncidence(type=type, channel = "mobil-app", subject = subject, detail = detail, files = files){ code ->
            RegisterUserClient.RegisterResponse(code)
        }


    }
}