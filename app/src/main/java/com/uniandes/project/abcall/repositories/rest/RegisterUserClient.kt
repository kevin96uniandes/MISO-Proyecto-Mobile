package com.uniandes.project.abcall.repositories.rest

import android.util.Log
import com.uniandes.project.abcall.config.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterUserClient {

    fun registerUser(fullName: String, username: String, password: String, checkPassword: String, callback: (Any?) -> Unit) {

        val body = UserRegisterRequestBody(
            fullName = fullName,
            username = username,
            password = password,
            checkPassword = checkPassword

        )

        RetrofitClient.apiService.register(userRegisterRequestBody = body).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    response.body()?.code
                } else {
                    Log.e("AuthClient", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e("MainActivity", "Failure: ${t.message}")
            }
        })

    }



    data class UserRegisterRequestBody(
        val fullName: String,
        val username: String,
        val password: String,
        val checkPassword: String
    )

    data class RegisterResponse(
        val code: Any?
    )


}

internal fun RegisterUserClient.RegisterResponse.toAuth() =
    this.code