package com.uniandes.project.abcall.repositories.rest

import android.util.Log
import com.uniandes.project.abcall.config.ApiResult
import com.uniandes.project.abcall.config.RetrofitClient
import com.uniandes.project.abcall.repositories.rest.RegisterUserClient.RegisterResponse
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IncidenceClient {

    fun createIncidence(body: CreateIncidenceRequest, callback: (ApiResult<Unit>) -> Unit) {
        val multipartBody = body.toCreateIncidenceRequest()
        RetrofitClient.apiService.createIncidence(
            multipartBody.personId,
            multipartBody.type,
            multipartBody.channel,
            multipartBody.subject,
            multipartBody.detail,
            multipartBody.files
        ).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {

                } else {
                    val errorMessage = response.errorBody()?.string()
                    callback(ApiResult.Error(response.code(), errorMessage))
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.e("MainActivity", "Failure: ${t.message}")
                callback(ApiResult.NetworkError)
            }
        })
    }


    data class CreateIncidenceRequest(
        val personId: Int,
        val type: String,
        val channel: String,
        val subject: String,
        val detail: String
    )

    data class CreateIncidenceRequestBody(
        val personId: RequestBody,
        val type: RequestBody,
        val channel: RequestBody,
        val subject: RequestBody,
        val detail: RequestBody,
        val files: MutableList<MultipartBody.Part> = mutableListOf()
    )

    internal fun CreateIncidenceRequest.toCreateIncidenceRequest() =
        CreateIncidenceRequestBody(
            personId = RequestBody.create(MediaType.parse("text/plain"), personId.toString()),
            type = RequestBody.create(MediaType.parse("text/plain"), type),
            channel = RequestBody.create(MediaType.parse("text/plain"), channel),
            subject = RequestBody.create(MediaType.parse("text/plain"), subject),
            detail = RequestBody.create(MediaType.parse("text/plain"), detail),
            files = mutableListOf()
        )

}