package com.uniandes.project.abcall.config

import okhttp3.Interceptor
import okhttp3.Response

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        println("Encabezados de la solicitud:")
        val headers = request.headers()
        for (i in 0 until headers.size()) {
            println("${headers.name(i)}: ${headers.value(i)}")
        }

        return chain.proceed(request)
    }
}