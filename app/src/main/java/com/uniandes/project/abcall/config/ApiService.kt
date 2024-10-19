package com.uniandes.project.abcall.config

import com.uniandes.project.abcall.repositories.rest.AuthClient
import com.uniandes.project.abcall.repositories.rest.RegisterUserClient

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/user/auth/login")
    fun login(@Body loginRequestBody: AuthClient.LoginRequestBody): Call<AuthClient.LoginResponse>

    @POST("user/auth/register")
    fun register(@Body userRegisterRequestBody: RegisterUserClient.UserRegisterRequestBody): Call<RegisterUserClient.RegisterResponse>

}