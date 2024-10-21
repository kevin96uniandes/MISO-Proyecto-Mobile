package com.uniandes.project.abcall.repositories.rest

import android.util.Log
import com.uniandes.project.abcall.config.RetrofitClient
import com.uniandes.project.abcall.models.Auth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthClient {

    fun authenticate(username: String, password: String, callback: (Auth?) -> Unit) {

        val body = LoginRequestBody(
            username = username,
            password = password
        )

        RetrofitClient.apiService.login(loginRequestBody = body).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val resp = response.body()
                    val auth = resp?.toAuth()
                    callback(auth)
                } else {
                    Log.e("AuthClient", "Error: ${response.errorBody()?.string()}")
                    callback(null) // En caso de error, devolver null
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("MainActivity", "Failure: ${t.message}")
                callback(null)
            }
        })

    }

    data class LoginRequestBody(
        val username: String,
        val password: String
    )

    data class LoginResponse(
        val token: String
    )

}

internal fun AuthClient.LoginResponse.toAuth() =
    Auth (
        token = this.token
    )