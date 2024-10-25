package com.uniandes.project.abcall.repositories.rest

import android.util.Log
import com.uniandes.project.abcall.config.RetrofitClient
import com.uniandes.project.abcall.models.Auth
import com.uniandes.project.abcall.repositories.rest.RegisterUserClient.RegisterResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateIncidence {

    fun createIncidence(type: Int, channel: String = "mobil-app", subject: String, detail: String, files: List<MultipartBody.Part>){
        val body = CreateIncidenceRequestBody(
            type = type,
            channel = channel,
            subject = subject,
            detail = detail
        )

        RetrofitClient.apiService.createIncidence(body =body, files =files).enqueue(object : Callback<CreateIncidenceResponse>{
            override fun onResponse(call: Call<CreateIncidenceResponse>, response: Response<CreateIncidenceResponse>){
                if (response.isSuccessful){
                    response.body()?.code
                }else {
                    Log.e("AuthClient", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<CreateIncidenceResponse>, t: Throwable) {
                Log.e("MainActivity", "Failure: ${t.message}")
            }

        })


    }


    data class CreateIncidenceRequestBody(
        val type: Int,
        val channel: String,
        val subject: String,
        val detail: String
    )

    data class CreateIncidenceResponse(
        val code: String
    )

}


internal fun CreateIncidence.CreateIncidenceResponse.toAuth() =
    Auth (
        token = ""
    )


