package com.example.lifesum.application

import androidx.appcompat.app.AppCompatDelegate
import com.example.authentication.interceptor.TokenInterceptor
import com.example.base.BaseApplication
import com.example.lifesum.BuildConfig
import com.example.lifesum.di.FeaturesKoinModules
import okhttp3.Interceptor
import org.koin.core.module.Module

class LifeSumApp : BaseApplication() {

    override val modulesList: ArrayList<Module>
        get() = FeaturesKoinModules.allModules
    override val interceptors: List<Interceptor>
        get() {
            val list = mutableListOf(
                TokenInterceptor()
            )
            return list
        }
    override val baseUrl: String
        get() = BuildConfig.BASE_URL

    override fun onCreate() {
        FeaturesKoinModules.init()
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

}