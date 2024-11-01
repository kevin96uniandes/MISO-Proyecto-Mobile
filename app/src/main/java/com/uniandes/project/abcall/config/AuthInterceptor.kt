package com.uniandes.project.abcall.config

import android.util.Log
import com.uniandes.project.abcall.enums.Technology
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenProvider: () -> String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val unauthenticatedPaths = listOf("/user/auth/login", "/user/register/user")

        val shouldSkipAuth = unauthenticatedPaths.any { path ->
            originalRequest.url().encodedPath().startsWith(path)
        }

        if (shouldSkipAuth) {
            return chain.proceed(originalRequest)
        }

        val token = tokenProvider()

        val requestBuilder = originalRequest.newBuilder()
        if (token.isNotEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
            requestBuilder.addHeader("Technology", Technology.MOBILE.name)
        }

        val request = requestBuilder.build()

        request.headers().names().forEach() { header ->
            Log.d("RequestHeader", header)
        }
        return chain.proceed(request)
    }
}

