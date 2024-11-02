package com.uniandes.project.abcall.config

import com.uniandes.project.abcall.models.History
import com.uniandes.project.abcall.models.Incident
import com.uniandes.project.abcall.repositories.rest.AuthClient
import com.uniandes.project.abcall.repositories.rest.RegisterUserClient

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("/user/auth/login")
    fun login(@Body loginRequestBody: AuthClient.LoginRequestBody): Call<AuthClient.LoginResponse>

    @POST("user/register/user")
    fun register(@Body userRegisterRequestBody: RegisterUserClient.UserRegisterRequestBody): Call<RegisterUserClient.RegisterResponse>

    @GET("incident/person/{id}")
    fun getIncidentsByPerson(@Path("id") id: Int): Call<List<Incident>>

    @GET("incident/get/{id}")
    fun findIncidentById(@Path("id") id: Int): Call<Incident>

    @GET("incident/history/{id}")
    fun findHistoryByIncident(@Path("id") id: Int): Call<List<History>>
}