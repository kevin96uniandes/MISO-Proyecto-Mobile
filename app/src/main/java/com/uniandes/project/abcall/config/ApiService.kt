package com.uniandes.project.abcall.config

import com.uniandes.project.abcall.repositories.rest.AuthClient
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/user/auth/login")
    fun login(@Body loginRequestBody: AuthClient.LoginRequestBody): Call<AuthClient.LoginResponse>
}