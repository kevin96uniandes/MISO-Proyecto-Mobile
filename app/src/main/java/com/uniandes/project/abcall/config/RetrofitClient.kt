package com.uniandes.project.abcall.config

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val BASE_URL = "http://192.168.18.14:3000"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /*
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

     */

    var apiService: ApiService = retrofit.create(ApiService::class.java)
        private set // Mantener la propiedad privada para evitar asignaciones externas

    fun setApiService(mock: ApiService) {
        apiService = mock // Permitir que se establezca un mock para pruebas
    }
}