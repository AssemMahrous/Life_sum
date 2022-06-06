package com.example.authentication.interceptor

import com.example.authentication.APIS.Headers.KEY_AUTHORIZATION
import com.example.authentication.BuildConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


class TokenInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        val newRequest = appendAuthHeader(builder)
        return chain.proceed(newRequest)
    }

    private fun appendAuthHeader(builder: Request.Builder) = builder.apply {
        addHeader(KEY_AUTHORIZATION, BuildConfig.AUTH_TOKEN)
    }.build()
}