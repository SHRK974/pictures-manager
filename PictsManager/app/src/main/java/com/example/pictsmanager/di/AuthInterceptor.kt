package com.example.pictsmanager.di


import android.util.Log
import com.example.pictsmanager.domain.util.AppPreferences
import com.example.pictsmanager.presentation.user.UserViewModel
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = AppPreferences.accessTokenAsString
        val request = chain.request()
        if (accessToken == null) {
            Log.w("Auth", "Token empty")
            return chain.proceed(request)
        }
        Log.w("Auth", accessToken)

        val response = chain.proceed(request.newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build())

        if (response.code == 401 || response.code == 500) {
            AppPreferences.accessTokenAsString = null
        }
        return response
    }
}