package com.uniandes.project.abcall.repositories.rest

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.uniandes.project.abcall.config.ApiResult
import com.uniandes.project.abcall.config.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardPercentageClient {

    fun getBoardPercentage(body: BoardPercentageRequestBody, callback: (ApiResult<BoardPercentageResponse>) -> Unit) {
        RetrofitClient.apiService.getBoardPercentage(
            channelId = body.channelId,
            stateId = body.stateId,
            startDate = body.startDate,
            endDate = body.endDate
        ).enqueue(object : Callback<BoardPercentageResponse> {
            override fun onResponse(
                call: Call<BoardPercentageResponse>,
                response: Response<BoardPercentageResponse>
            ) {
                if (response.isSuccessful) {
                    Log.e("board success", response.body()!!.toString())
                    callback(ApiResult.Success(response.body()!!))
                }else {
                    Log.e("board error", response.errorBody()!!.string())
                    val errorMessage = response.errorBody()?.string()
                    callback(ApiResult.Error(response.code(), errorMessage))
                }
            }

            override fun onFailure(call: Call<BoardPercentageResponse>, t: Throwable) {
                Log.e("MainActivity", "Failure: ${t.message}")
                callback(ApiResult.NetworkError)
            }

        })
    }

    data class BoardPercentageRequestBody(
        val channelId: Int?,
        val stateId: Int?,
        val startDate: String,
        val endDate: String
    )

    data class BoardPercentageResponse(
        @SerializedName("channels")
        val channels: List<Channels>
    ){
        data class Channels(
            @SerializedName("channel")
            val channel: String,
            @SerializedName("value")
            val value: Double
        )
    }
}