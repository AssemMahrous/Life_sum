package com.example.base.di

import com.example.base.utils.CustomHttpLoggingInterceptor
import com.google.gson.Gson
import com.moczul.ok2curl.CurlInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

object NetworkModule {
    const val QUALIFIER_BASE_URL = "base-url"

    lateinit var module: Module
        private set

    fun init(
        baseUrl: String,
        interceptors: List<Interceptor>,
    ) {

        this.module = module {
            single(named(QUALIFIER_BASE_URL)) { baseUrl }
            single { interceptors }
            single { provideOkHttp(get()) }
            single { provideRetrofit(get(named(QUALIFIER_BASE_URL)), get(), get()) }
        }
    }

    private fun provideRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        gson: Gson,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClient)
        .build()

    private fun provideOkHttp(
        interceptors: List<Interceptor>,
    ): OkHttpClient {
        val logging = CustomHttpLoggingInterceptor(
            object :
                CustomHttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Timber.tag("OkHttp").d(message)
                }
            })
            .apply {
                // for remove any sensitive headers from the request
                redactHeader("Authorization")
                redactHeader("Cookie")
                redactHeader("FCMToken")
            }

        logging.level = CustomHttpLoggingInterceptor.Level.BODY

        val timeout: Long = 60
        val okHttpClientBuilder = OkHttpClient.Builder()
            .readTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(timeout, TimeUnit.SECONDS)
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .callTimeout(timeout, TimeUnit.SECONDS)
            .addNetworkInterceptor(logging)

        //enabled here in all cases if this code to be shipped should add condition for removal
        okHttpClientBuilder
            .addNetworkInterceptor(CurlInterceptor { Timber.tag("Ok2Curl").d(it) })

        interceptors.forEach { okHttpClientBuilder.interceptors().add(it) }

        return okHttpClientBuilder.build()
    }


}
