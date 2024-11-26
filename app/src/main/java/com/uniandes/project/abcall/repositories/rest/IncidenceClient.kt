package com.uniandes.project.abcall.repositories.rest

import android.util.Log
import com.uniandes.project.abcall.config.ApiResult
import com.uniandes.project.abcall.config.RetrofitClient
import com.uniandes.project.abcall.enums.Technology
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class IncidenceClient {

    fun createIncidence(body: CreateIncidenceRequest, callback: (ApiResult<IncidenceResponse>) -> Unit) {
        val multipartBody = body.toCreateIncidenceRequest()
        RetrofitClient.apiService.createIncidence(
            multipartBody.personId,
            multipartBody.type,
            multipartBody.channel,
            multipartBody.subject,
            multipartBody.detail,
            multipartBody.idCompany,
            multipartBody.files,
        ).enqueue(object : Callback<IncidenceResponse> {
            override fun onResponse(call: Call<IncidenceResponse>, response: Response<IncidenceResponse>) {
                if (response.isSuccessful) {
                    callback(ApiResult.Success(response.body()!!)) // Llama a callback en caso de éxito
                } else {
                    Log.d("Error creating incidence", response.errorBody()!!.string())
                    val errorMessage = response.errorBody()?.string()
                    callback(ApiResult.Error(response.code(), errorMessage))
                }
            }

            override fun onFailure(call: Call<IncidenceResponse>, t: Throwable) {
                callback(ApiResult.NetworkError)
            }
        })
    }

    data class CreateIncidenceRequest(
        val personId: Int,
        val type: String,
        val subject: String,
        val detail: String,
        val files: List<File>,
        val idCompany: Int
    )

    data class CreateIncidenceRequestBody(
        val personId: RequestBody,
        val type: RequestBody,
        val channel: RequestBody,
        val subject: RequestBody,
        val detail: RequestBody,
        val files: List<MultipartBody.Part>,
        val idCompany: RequestBody
    )

    private fun CreateIncidenceRequest.toCreateIncidenceRequest(): CreateIncidenceRequestBody {
        val filesMultipart = files.mapNotNull { fileToMultipart(it) }

        return CreateIncidenceRequestBody(
            personId = RequestBody.create(MediaType.parse("text/plain"), personId.toString()),
            type = RequestBody.create(MediaType.parse("text/plain"), type),
            channel = RequestBody.create(MediaType.parse("text/plain"), Technology.MOBILE.channel),
            subject = RequestBody.create(MediaType.parse("text/plain"), subject),
            detail = RequestBody.create(MediaType.parse("text/plain"), detail),
            files = filesMultipart,
            idCompany = RequestBody.create(MediaType.parse("text/plain"), idCompany.toString()),
        )
    }

    private fun fileToMultipart(file: File): MultipartBody.Part? {
        val fileName = file.name
        val mimeType = when(file.extension.lowercase()) {
            "jpg" -> "image/jpeg"
            "png" -> "image/png"
            "pdf" -> "application/pdf"
            else -> "application/octet-stream"
        }

        return try {
            val requestFile = RequestBody.create(MediaType.parse(mimeType), file)

            MultipartBody.Part.createFormData("files", fileName, requestFile)
        } catch (e: Exception) {
            Log.e("IncidenceClient", "Error creating MultipartBody.Part from file: ${file.absolutePath}", e)
            null
        }
    }

    data class IncidenceResponse(
        val code: Int
    )
}
