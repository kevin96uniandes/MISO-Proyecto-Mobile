package com.uniandes.project.abcall.config

import com.uniandes.project.abcall.repositories.rest.AuthClient
import com.uniandes.project.abcall.repositories.rest.RegisterUserClient
import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @POST("/user/auth/login")
    fun login(@Body loginRequestBody: AuthClient.LoginRequestBody): Call<AuthClient.LoginResponse>

    @POST("user/register/user")
    fun register(@Body userRegisterRequestBody: RegisterUserClient.UserRegisterRequestBody): Call<RegisterUserClient.RegisterResponse>

    @Multipart
    @POST("/incident/create")
    fun createIncidence(
        @Part("person_id") personId: RequestBody,
        @Part("incidentType") incidentType: RequestBody,
        @Part("incidentChannel") incidentChannel: RequestBody,
        @Part("incidentSubject") incidentSubject: RequestBody,
        @Part("incidentDetail") incidentDetail: RequestBody,
        @Part files: List<MultipartBody.Part?>
    ): Call<Unit>
}