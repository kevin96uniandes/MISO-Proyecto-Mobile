package com.uniandes.project.abcall.config

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    //private val BASE_URL = "http://34.111.136.182"
    private const val BASE_URL = "http://192.168.18.14:5000"

    private lateinit var authToken: String

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.MINUTES)
            .writeTimeout(30, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.MINUTES)
            .addInterceptor(AuthInterceptor(::getAuthToken))
            .addInterceptor(LoggingInterceptor())
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    var apiService: ApiService = retrofit.create(ApiService::class.java)
        private set

    fun setApiService(mock: ApiService) {
        apiService = mock
    }

    private fun getAuthToken(): String {
        return authToken
    }

    fun updateAuthToken(token: String) {
        authToken = token
    }
}