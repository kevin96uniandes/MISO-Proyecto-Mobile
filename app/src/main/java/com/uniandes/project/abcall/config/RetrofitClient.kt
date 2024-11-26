package com.uniandes.project.abcall.config

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private var BASE_URL = "http://34.149.73.72"
    //private var BASE_URL = "http://192.168.18.14:5000"
    //private var BASE_URL = "http://192.168.128.182:5000"

    private lateinit var authToken: String

    // Cliente OkHttp que usaremos en Retrofit
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.MINUTES)
            .writeTimeout(30, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.MINUTES)
            .addInterceptor(AuthInterceptor(::getAuthToken))
            .addInterceptor(LoggingInterceptor())
            .build()
    }

    // Propiedad Retrofit privada que se actualiza al cambiar la base URL
    private var retrofit: Retrofit = createRetrofit(BASE_URL)

    // Instancia del servicio API que se actualizará al cambiar la base URL
    var apiService: ApiService = retrofit.create(ApiService::class.java)
        private set

    // Función que permite actualizar la instancia de ApiService cuando se cambia la URL
    fun setBaseUrl(baseUrl: String) {
        BASE_URL = baseUrl
        retrofit = createRetrofit(BASE_URL)
        apiService = retrofit.create(ApiService::class.java)
    }

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Método para pasar un mock de ApiService (para pruebas)
    fun setApiService(mock: ApiService) {
        apiService = mock
    }

    private fun getAuthToken(): String {
        return authToken
    }

    // Actualiza el token de autenticación
    fun updateAuthToken(token: String) {
        authToken = token
    }
}
