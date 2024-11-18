package com.uniandes.project.abcall.repositories.rest

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.uniandes.project.abcall.config.ApiResult
import com.uniandes.project.abcall.config.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardSummaryClient {

    fun getBoardSummary(body: BoardSummaryRequestBody, callback: (ApiResult<BoardSummaryResponse>) -> Unit) {
        RetrofitClient.apiService.getBoardSummary(
            channelId = body.channelId,
            stateId = body.stateId,
            startDate = body.startDate,
            endDate = body.endDate
        ).enqueue(object : Callback<BoardSummaryResponse> {
            override fun onResponse(
                call: Call<BoardSummaryResponse>,
                response: Response<BoardSummaryResponse>
            ) {
                if (response.isSuccessful) {
                    Log.e("board success", response.body()!!.toString())
                    callback(ApiResult.Success(response.body()!!))
                }else {
                    val errorMessage = response.errorBody()?.string()
                    callback(ApiResult.Error(response.code(), errorMessage))
                }
            }

            override fun onFailure(call: Call<BoardSummaryResponse>, t: Throwable) {
                Log.e("MainActivity", "Failure: ${t.message}")
                callback(ApiResult.NetworkError)
            }

        })
    }

    data class BoardSummaryRequestBody(
        val channelId: Int?,
        val stateId: Int?,
        val startDate: String,
        val endDate: String
    )

    data class BoardSummaryResponse(
        @SerializedName("incidentes")
        val incidences: List<Incidence>,
        @SerializedName("total")
        val total: Int
    ) {
        data class Incidence(
            @SerializedName("asunto")
            val subject: String,
            @SerializedName("canal")
            val channel: String,
            @SerializedName("codigo")
            val code: String,
            @SerializedName("estado")
            val estate: String,
            @SerializedName("fecha_actualizacion")
            val updateDate: String,
            @SerializedName("fecha_creacion")
            val createDate: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("tipo")
            val type: String
        )
    }
}