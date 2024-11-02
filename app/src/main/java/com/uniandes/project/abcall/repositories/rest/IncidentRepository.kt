package com.uniandes.project.abcall.repositories.rest

import android.util.Log
import com.google.gson.Gson
import com.uniandes.project.abcall.config.ApiResult
import com.uniandes.project.abcall.config.RetrofitClient
import com.uniandes.project.abcall.models.History
import com.uniandes.project.abcall.models.Incident
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IncidentRepository {
    fun getIncidentsByPerson(id: Int, callback: (ApiResult<List<Incident>>) -> Unit) {
        RetrofitClient.apiService.getIncidentsByPerson(id = id).enqueue(object :
            Callback<List<Incident>> {
            override fun onResponse(call: Call<List<Incident>>, response: Response<List<Incident>>) {
                if (response.isSuccessful) {
                    val json = Gson().toJson(response.body())
                    Log.d("IncidentResult", "Resultado: ${json}")
                    callback(ApiResult.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    callback(ApiResult.Error(response.code(), errorMessage))
                }
            }
            override fun onFailure(call: Call<List<Incident>>, t: Throwable) {
                Log.e("MainActivity", "Failure: ${t.message}")
                callback(ApiResult.NetworkError)
            }
        })
    }

    fun findIncidentById(id: Int, callback: (ApiResult<Incident>) -> Unit) {
        RetrofitClient.apiService.findIncidentById(id = id).enqueue(object :
            Callback<Incident> {
            override fun onResponse(call: Call<Incident>, response: Response<Incident>) {
                if (response.isSuccessful) {
                    val json = Gson().toJson(response.body())
                    Log.d("IncidentResult", "Resultado: ${json}")
                    callback(ApiResult.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    callback(ApiResult.Error(response.code(), errorMessage))
                }
            }
            override fun onFailure(call: Call<Incident>, t: Throwable) {
                Log.e("MainActivity", "Failure: ${t.message}")
                callback(ApiResult.NetworkError)
            }
        })
    }

    fun findHistoryByIncident(id: Int, callback: (ApiResult<List<History>>) -> Unit) {
        RetrofitClient.apiService.findHistoryByIncident(id = id).enqueue(object :
            Callback<List<History>> {
            override fun onResponse(call: Call<List<History>>, response: Response<List<History>>) {
                val json = Gson().toJson(response.body())
                if (response.isSuccessful) {
                    Log.d("HistoryResult", "Resultado: ${json}")
                    callback(ApiResult.Success(response.body()!!))
                } else {
                    Log.d("HistoryResultError", "Resultado: ${response.errorBody()?.string()}")
                    val errorMessage = response.errorBody()?.string()
                    callback(ApiResult.Error(response.code(), errorMessage))
                }
            }
            override fun onFailure(call: Call<List<History>>, t: Throwable) {
                Log.e("MainActivity", "Failure: ${t.message}")
                callback(ApiResult.NetworkError)
            }
        })
    }
}
