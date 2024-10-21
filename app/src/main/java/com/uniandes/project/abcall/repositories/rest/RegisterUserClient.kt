package com.uniandes.project.abcall.repositories.rest

import android.util.Log
import com.google.gson.Gson
import com.uniandes.project.abcall.config.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterUserClient {

    fun registerUser(body: UserRegisterRequestBody, callback: (Int?) -> Unit) {
        val gson = Gson()
        Log.d("UserRegisterRequest", gson.toJson(body))
        RetrofitClient.apiService.register(userRegisterRequestBody = body).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val code = response.body()!!.code
                    callback(code)
                } else {
                    Log.e("AuthClient", "Error: ${response.errorBody()?.string()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e("MainActivity", "Failure: ${t.message}")
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
