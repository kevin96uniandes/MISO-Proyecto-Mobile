package com.uniandes.project.abcall.repositories.rest

import android.util.Log
import com.google.gson.Gson
import com.uniandes.project.abcall.config.ApiResult
import com.uniandes.project.abcall.config.RetrofitClient
import com.uniandes.project.abcall.exceptions.UsernameAlreadyExistsException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterUserClient {

    fun registerUser(body: UserRegisterRequestBody, callback: (ApiResult<RegisterResponse>) -> Unit) {
        val gson = Gson()
        Log.d("UserRegisterRequest", gson.toJson(body))
        RetrofitClient.apiService.register(userRegisterRequestBody = body).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    callback(ApiResult.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                    callback(ApiResult.Error(response.code(), errorMessage))
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e("MainActivity", "Failure: ${t.message}")
                callback(ApiResult.NetworkError)
            }
        })
    }
    data class UserRegisterRequestBody(
        val usuario: String,
        val apellidos: String,
        val nombres: String,
        val contrasena: String,
        val tipo_identificacion: Int,
        val numero_identificacion: Long,
        val confirmar_contrasena: String,
        val telefono: String,
        val correo_electronico: String
    )

    data class RegisterResponse(
        val code: Int
    )
}
